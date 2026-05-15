package br.com.evolucaoparking.config;

import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

public class MigratingDataSource extends AbstractDataSource {

    private final DataSource target;
    private final AtomicBoolean migrated = new AtomicBoolean();

    public MigratingDataSource(DataSource target) {
        this.target = target;
    }

    @Override
    public Connection getConnection() throws SQLException {
        migrarSeNecessario();
        return target.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        migrarSeNecessario();
        return target.getConnection(username, password);
    }

    private void migrarSeNecessario() {
        if (!migrated.compareAndSet(false, true)) {
            return;
        }
        try (Connection conn = target.getConnection()) {
            migrarVagas(conn);
            if (!tabelaExiste(conn, "REGISTROS")) {
                return;
            }
            adicionarColuna(conn, "REGISTROS", "CODIGO_RECIBO", "VARCHAR(36)");
            adicionarColuna(conn, "REGISTROS", "NOME_MOTORISTA", "VARCHAR(120)");
            adicionarColuna(conn, "REGISTROS", "TIPO_VEICULO", "VARCHAR(10)");
            adicionarColuna(conn, "REGISTROS", "MODALIDADE", "VARCHAR(20)");
            adicionarColuna(conn, "REGISTROS", "TURNO_ENTRADA_ID", "BIGINT");
            adicionarColuna(conn, "REGISTROS", "TURNO_SAIDA_ID", "BIGINT");

            try (Statement st = conn.createStatement()) {
                st.executeUpdate("UPDATE registros SET codigo_recibo = 'R' || id WHERE codigo_recibo IS NULL");
                if (colunaExiste(conn, "REGISTROS", "MODELO")) {
                    st.executeUpdate("""
                            UPDATE registros SET nome_motorista = COALESCE(NULLIF(modelo, ''), 'Sem nome')
                            WHERE nome_motorista IS NULL
                            """);
                } else {
                    st.executeUpdate("UPDATE registros SET nome_motorista = 'Sem nome' WHERE nome_motorista IS NULL");
                }
                st.executeUpdate("UPDATE registros SET tipo_veiculo = 'CARRO' WHERE tipo_veiculo IS NULL");
                st.executeUpdate("UPDATE registros SET modalidade = 'FRACAO_HORA' WHERE modalidade IS NULL");
            }
        } catch (SQLException ex) {
            migrated.set(false);
            throw new IllegalStateException("Falha ao migrar tabela registros", ex);
        }
    }

    private void migrarVagas(Connection conn) throws SQLException {
        if (!tabelaExiste(conn, "VAGAS")) {
            return;
        }
        adicionarColuna(conn, "VAGAS", "TIPO_VEICULO", "VARCHAR(10) NOT NULL DEFAULT 'CARRO'");
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("UPDATE vagas SET tipo_veiculo = 'CARRO' WHERE tipo_veiculo IS NULL");
        }
        removerIndiceUnicoAntigoNumero(conn);
        try (Statement st = conn.createStatement()) {
            st.execute("""
                    CREATE UNIQUE INDEX IF NOT EXISTS uk_vagas_numero_tipo
                    ON vagas (numero, tipo_veiculo)
                    """);
        } catch (SQLException ignored) {
            // índice já existe via Hibernate
        }
    }

    private void removerIndiceUnicoAntigoNumero(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("""
                     SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                     WHERE TABLE_NAME = 'VAGAS' AND CONSTRAINT_TYPE = 'UNIQUE'
                     """)) {
            while (rs.next()) {
                String nome = rs.getString(1);
                if (nome != null && !nome.equalsIgnoreCase("uk_vagas_numero_tipo")) {
                    executarSilencioso(conn, "ALTER TABLE vagas DROP CONSTRAINT " + nome);
                }
            }
        }
        executarSilencioso(conn, "DROP INDEX IF EXISTS UK_R3SEKN14G8FFSVQJKBCD8HINK_INDEX_A");
        executarSilencioso(conn, "ALTER TABLE vagas DROP CONSTRAINT IF EXISTS UK_R3SEKN14G8FFSVQJKBCD8HINK");
    }

    private void executarSilencioso(Connection conn, String sql) {
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException ignored) {
            // constraint/index pode não existir
        }
    }

    private void adicionarColuna(Connection conn, String tabela, String coluna, String tipo) throws SQLException {
        if (colunaExiste(conn, tabela, coluna)) {
            return;
        }
        try (Statement st = conn.createStatement()) {
            st.execute("ALTER TABLE " + tabela + " ADD COLUMN " + coluna + " " + tipo);
        }
    }

    private boolean tabelaExiste(Connection conn, String tabela) throws SQLException {
        return metaExiste(conn.getMetaData().getTables(null, null, tabela, new String[]{"TABLE"}))
                || metaExiste(conn.getMetaData().getTables(null, "PUBLIC", tabela, new String[]{"TABLE"}));
    }

    private boolean colunaExiste(Connection conn, String tabela, String coluna) throws SQLException {
        return metaExiste(conn.getMetaData().getColumns(null, null, tabela, coluna))
                || metaExiste(conn.getMetaData().getColumns(null, "PUBLIC", tabela, coluna));
    }

    private boolean metaExiste(ResultSet rs) throws SQLException {
        try (rs) {
            return rs.next();
        }
    }
}
