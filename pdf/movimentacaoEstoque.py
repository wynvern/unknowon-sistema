#pip install mysql-connector-python
import mysql.connector
from datetime import datetime
from fpdf import FPDF
import sys
import time
import configparser


config = configparser.ConfigParser()
config.read('../config.ini')

entradaData = sys.argv[1]
saidaData = sys.argv[2]

date_objectEntrada = time.mktime(datetime.strptime(entradaData, "%d/%m/%Y").timetuple())
date_objectEntrada = datetime.fromtimestamp(date_objectEntrada)

date_objectSaida = time.mktime(datetime.strptime(saidaData, "%d/%m/%Y").timetuple())
date_objectSaida = datetime.fromtimestamp(date_objectSaida)

# Establish a connection to your MariaDB database
conn = mysql.connector.connect(
    host = config.get('mysql', 'hostname'),
    user = config.get('mysql', 'username'),
    password = config.get('mysql', 'password'),
    database = config.get('mysql', 'database')
)

cursor = conn.cursor()

dataEntradaMysql = date_objectEntrada.strftime('%Y-%m-%d')
dataEntradaFormatada = date_objectEntrada.strftime("%d/%m/%Y")

dataSaidaMysql = date_objectEntrada.strftime('%Y-%m-%d')
dataEntradaFormatada = date_objectEntrada.strftime("%d/%m/%Y")

cursor.execute(f"SELECT * FROM notasItens WHERE DATE(criadoEm) BETWEEN '{dataEntradaMysql}' AND '{dataSaidaMysql}';")
notasItens = cursor.fetchall()

cursor.execute(f"SELECT * FROM produtos;")
produtos = cursor.fetchall()

cursor.execute(f"SELECT * FROM notas;")
notas = cursor.fetchall()

movimentacao = {}
notasMov = {}

for nota in notas:
     notaC = {}
     notaC['tipo'] = nota[2]
     notaC['numeroNota'] = nota[4]
     notasMov[nota[0]] = notaC


for produto in produtos:
    informacoes = {}
    informacoes['descricao'] = produto[2]
    informacoes['valor'] = produto[3]
    informacoes['entrada'] = 0
    informacoes['saida'] = 0
    informacoes['saldo'] = 0
    informacoes['saldoAnterior'] = 0
    informacoes['cliente/fornecedor'] = ''
    informacoes['entrada'] = False
    
    movimentacao[produto[0]] = informacoes


for item in notasItens:
    # changing by id

    if (notasMov[item[1]]['tipo'] == 'Entrada'):
        movimentacao[item[2]]['entrada'] += item[4]
        movimentacao[item[2]]['saldo'] += item[3]
    else:
        movimentacao[item[2]]['saida'] += item[4]
        movimentacao[item[2]]['saldo'] -= item[3]



print(movimentacao)
conn.close()