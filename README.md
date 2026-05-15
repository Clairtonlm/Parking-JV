# EvoluçãoParking

Sistema web de controle de estacionamento em **Java 17** com **Spring Boot 3**, login por turno, cadastro de funcionários e recibos de entrada/saída.

## Funcionalidades

- **Admin:** cadastro de funcionários, histórico de turnos (login/logout), carros/motos por turno, caixa
- **Funcionário:** login inicia o turno, logout encerra; entrada/saída de veículos com recibo para impressão
- **Motorista:** placa + nome na entrada; na saída informa placa ou nome e vê o valor
- **Modalidades:** valor fixo 24h, fração (R$ 5/h), bloco 5h (R$ 20), bloco 10h (R$ 45) com excedente de R$ 2 / 15 min

## Requisitos

- JDK 17+
- Maven 3.9+ ou `mvnw.cmd`

## Executar

```powershell
.\mvnw.cmd spring-boot:run
```

Acesse: [http://localhost:8080](http://localhost:8080)

### Usuários padrão

| Perfil | Login | Senha |
|--------|-------|-------|
| Admin | `admin` | `admin123` |
| Funcionário (demo) | `joao` | `func123` |

## Tarifas (`application.properties`)

| Propriedade | Padrão |
|-------------|--------|
| `app.parking.valor-fixo-24h` | 40.00 |
| `app.parking.tarifa-hora` | 5.00 |
| `app.parking.tolerancia-minutos` | 15 |
| `app.parking.bloco5h-valor` | 20.00 |
| `app.parking.bloco10h-valor` | 45.00 |
| `app.parking.excesso-bloco15min` | 2.00 |

## Fluxo

1. Funcionário faz **login** → turno abre automaticamente
2. Registra **entrada** (placa, nome, tipo carro/moto, modalidade) → imprime recibo
3. Na **saída**, consulta valor por placa ou nome → imprime recibo com total
4. **Logout** encerra o turno
5. Admin acompanha tudo em `/admin`

## Erro de coluna no banco (após atualização)

Se aparecer erro tipo `CODIGO_RECIBO not found`, o banco H2 foi criado com versão antiga. Pare o servidor e apague a pasta `data/` na raiz do projeto (ou só os arquivos `evolucao-parking.*`), depois suba de novo. Os usuários padrão serão recriados.

## Testes

```powershell
.\mvnw.cmd test
```
