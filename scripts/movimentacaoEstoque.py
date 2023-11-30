#pip install mysql-connector-python
import mysql.connector
from datetime import datetime
from fpdf import FPDF
import sys
import time
import configparser


config = configparser.ConfigParser()
config.read('config.ini')

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

dataSaidaMysql = date_objectSaida.strftime('%Y-%m-%d')  # Assuming you have a date_objectSaida variable
dataSaidaFormatada = date_objectSaida.strftime("%d/%m/%Y")

cursor.execute(f"SELECT * FROM notasItens WHERE DATE(criadoEm) BETWEEN '{dataEntradaMysql}' AND '{dataSaidaMysql}';")

notasItens = cursor.fetchall()

cursor.execute(f"SELECT * FROM produtos;")
produtos = cursor.fetchall()

todosItens = {}

# Adiciona produtos no array
for produto in produtos:
    itemTemp = {}

    itemTemp['descricao'] = produto[2]
    itemTemp['codigo'] = produto[0]

    cursor.execute(f"SELECT * FROM notasItens WHERE DATE(criadoEm) < '{dataEntradaMysql}' AND id = '{itemTemp['codigo']}';")
    previous_notasItens = cursor.fetchall()

    itemTemp['saldoAnterior'] = 0       

    for antigaNotaItem in previous_notasItens:
        # Pegar a nota do item
        cursor.execute(f"SELECT * FROM notas WHERE id = '{antigaNotaItem[1]}';")
        nota = cursor.fetchall()
        nota = nota[0]

        # Pegar Fornecedor ou cCliente
        cursor.execute(f"SELECT * FROM entidades WHERE id = '{nota[1]}';")
        entidade = cursor.fetchall()
        entidade = entidade[0]
        
        if entidade[3] == 'Fornecedor':
            itemTemp['saldoAnterior'] += antigaNotaItem[4]
        else:
            itemTemp['saldoAnterior'] -= antigaNotaItem[4]

    itemTemp['notas'] = []

    todosItens[itemTemp['codigo']] = itemTemp


# adiciona as notas dos itens no produto
for notasItem in notasItens:
    itemTemp = {}

    # Pegar a nota do item
    cursor.execute(f"SELECT * FROM notas WHERE id = '{notasItem[1]}';")
    nota = cursor.fetchall()
    nota = nota[0]

    # Pegar Fornecedor ou cCliente
    cursor.execute(f"SELECT * FROM entidades WHERE id = '{nota[1]}';")
    entidade = cursor.fetchall()
    entidade = entidade[0]

    # Adiciona os valores
    itemTemp['data'] = nota[3]
    itemTemp['nota'] = nota[4]
    itemTemp['nomeEntidade'] = entidade[2]
    itemTemp['entrada'] = 0
    itemTemp['saida'] = 0
    itemTemp['saldo'] = 0 

    if entidade[3] == 'Fornecedor':
        itemTemp['entrada'] += notasItem[4]
        itemTemp['saldo'] += notasItem[3] * notasItem[4]
    else:
        itemTemp['saida'] += notasItem[4]
        itemTemp['saldo'] -= notasItem[3] * notasItem[4]


    todosItens[notasItem[2]]['notas'].append(itemTemp)
    
        


# PDF

pdf = FPDF()
pdf.add_page()
pdf.set_font("Arial", size=9)

pdf.image('C:\\Users\\wynvern\\Downloads\\PDF-gen\\banner.png', x=10, y=8, w=30)

pdf.set_font("Arial", style="B", size=16)  # Adjust the size as needed

# Add title to the header
pdf.cell(0, 10, 'Movimentação de Estoque', ln=True, align='C')

# Set font for the date
pdf.set_font("Arial", size=9)

current_date = datetime.now().strftime("%d/%m/%Y")
pdf.multi_cell(0, 5, f'Período Inicial: {dataEntradaFormatada}\nPeríodo Final: {dataSaidaFormatada}\nData Gerado: {current_date}', align='C')
pdf.ln()

def criarTabelaProduto(codigo, descricao, saldo_anterior):
    total_width = pdf.w - 2 * pdf.l_margin
    num_columns = 3
    header_labels = ["Código: " + str(codigo), descricao, "Saldo Anterior: " + str(saldo_anterior)]
    col_widths = [total_width * 0.14] * (num_columns - 1)
    col_widths.append(total_width * 0.16)

    total_table_width = pdf.w - col_widths[1] / 2
    start_x = pdf.l_margin  # Starting position

    align = ['L', 'C', 'R']

    # Tabela Produtos
    for i, header in enumerate(header_labels):
        pdf.set_x(start_x)
        pdf.set_font("Arial", 'B', 9)
        pdf.cell(col_widths[i], 5, txt=str(header_labels[i]), border=0, align=align[i], ln=False)
        pdf.set_font("Arial", '', 9)
        start_x += col_widths[i]  # Adjust the starting position for the next column

    pdf.ln(7)

def criarTabelaNotas(id):
    grey_color = (220, 220, 220)  # RGB values for grey
    white_color = (255, 255, 255)  # RGB values for white
    current_color = white_color
    total_width = pdf.w - 2 * pdf.l_margin
    num_columns = 6
    header_labels = ['Data', 'nota', 'Fornecedor/Cliente', 'Entrada', 'Saída', 'Saldo']
    header_labels_sys = ['data', 'nota', 'nomeEntidade', 'entrada', 'saida', 'saldo']
    col_widths = [total_width * 0.14] * (num_columns - 1)
    col_widths.append(total_width * 0.16)

    total_table_width = sum(col_widths)
    start_x = (pdf.w - total_table_width) / 2

    # Print data
    for nota in todosItens[id]['notas']:
        start_x = (pdf.w - total_table_width) / 2
        pdf.set_fill_color(*current_color)

        for i, header in enumerate(header_labels_sys):
            pdf.set_x(start_x)
            pdf.set_font("Arial", '', 9)
            pdf.cell(col_widths[i], 5, txt=str(nota[header]), border=0, align='C', ln=False)
            start_x += col_widths[i]

        current_color = white_color if current_color == grey_color else grey_color
        pdf.ln()

    pdf.ln(10)  # Add some space between rows


for x in todosItens:
    criarTabelaProduto(todosItens[x]['codigo'], todosItens[x]['descricao'], todosItens[x]['saldoAnterior'])
    criarTabelaNotas(x)



timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
pdf.output(f"movimentação_estoque_{timestamp}.pdf")
conn.close()