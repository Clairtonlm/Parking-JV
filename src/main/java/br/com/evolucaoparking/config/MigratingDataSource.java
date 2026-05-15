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
