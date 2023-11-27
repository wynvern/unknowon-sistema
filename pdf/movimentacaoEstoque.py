#pip install mysql-connector-python
import mysql.connector
from datetime import datetime
from fpdf import FPDF
import sys
import time
import configparser


config = configparser.ConfigParser()
config.read('../config.ini')

parameter_value = sys.argv[1]

# Convert the string to a datetime object
date_object = time.mktime(datetime.strptime(parameter_value, "%d/%m/%Y").timetuple())
date_object = datetime.fromtimestamp(date_object)

# Establish a connection to your MariaDB database
conn = mysql.connector.connect(
    host = config.get('mysql', 'hostname'),
    user = config.get('mysql', 'username'),
    password = config.get('mysql', 'password'),
    database = config.get('mysql', 'database')
)

cursor = conn.cursor()

# Mudar conforme necessidade
dataAte = date_object.strftime('%Y-%m-%d')
dataFormatada = date_object.strftime("%d/%m/%Y")

cursor.execute(f"SELECT * FROM notasItens WHERE DATE(criadoEm) <= '{dataAte}';")
notasItens = cursor.fetchall()

cursor.execute(f"SELECT * FROM produtos;")
produtos = cursor.fetchall()

movimentacao = {}

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




print(movimentacao)
conn.close()