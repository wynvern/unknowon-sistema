#pip install mysql-connector-python
import mysql.connector
from datetime import datetime
from fpdf import FPDF
import sys
import time
import configparser


config = configparser.ConfigParser()
config.read('config.ini')

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

notaPorProduto = {}


for produto in produtos:
    # Creates a dict key for each product
    notaPorProduto[produto[0]] = [produto[0], produto[2], produto[3], produto[6]]


for notaItem in notasItens:
    if len(notaPorProduto[notaItem[2]]) < 5: 
        notaPorProduto[notaItem[2]].append(0)
    
    if len(notaPorProduto[notaItem[2]]) < 6: 
        notaPorProduto[notaItem[2]].append(0)

    cursor.execute(f"SELECT * FROM notas WHERE id = {notaItem[1]};")
    dataNota = cursor.fetchall()
    
    if dataNota[0][2] == "Saida":
        notaPorProduto[notaItem[2]][4] -= notaItem[4] # Remove amount of items
        notaPorProduto[notaItem[2]][5] -= notaPorProduto[notaItem[2]][2] * notaItem[4]
    else:
        notaPorProduto[notaItem[2]][4] += notaItem[4] # Add amount of items
        notaPorProduto[notaItem[2]][5] += notaPorProduto[notaItem[2]][2] * notaItem[4]
    
# IdProduto = [id, descricao, valor un, un, qtd, valor total]


pdf = FPDF()
pdf.add_page()
pdf.set_font("Arial", size=9)

pdf.image('C:\\Users\\wynvern\\Downloads\\PDF-gen\\banner.png', x=10, y=8, w=30)

pdf.set_font("Arial", style="B", size=16)  # Adjust the size as needed

# Add title to the header
pdf.cell(0, 10, 'Inventário de Estoque', ln=True, align='C')

# Set font for the date
pdf.set_font("Arial", size=9)

current_date = datetime.now().strftime("%d/%m/%Y")
pdf.cell(0, 10, f'Período: {dataFormatada}', ln=True, align='C')
pdf.cell(0, 10, f'Data Gerado: {current_date}', ln=True, align='C')

# Calculate total width available for columns
total_width = pdf.w - 2 * pdf.l_margin
num_columns = 6  # Assuming 6 columns in your table

# Header row
header_labels = ["Código", "Descrição", "Valor Unitário", "Unidade", "Quantidade", "Valor Total"]

# Adjust the factor (0.15) as needed
col_widths = [total_width * 0.14] * (num_columns - 1)
col_widths.append(total_width * 0.16)  # Adjust the factor (0.16) as needed for the last column

total_table_width = sum(col_widths)

# Calculate the starting position to center the table
start_x = (pdf.w - total_table_width) / 2

pdf.ln()

# Header row
value = 0
for i, header in enumerate(header_labels):
    pdf.set_x(start_x + value)
    pdf.set_font("Arial", 'B', 9)
    pdf.cell(col_widths[i], 5, txt=header, border=0, align='C', ln=False)
    pdf.set_font("Arial", '', 9)
    if i < len(col_widths) - 1:  # Check if it's not the last element
        value += col_widths[i]
pdf.ln()

grey_color = (220, 220, 220)  # RGB values for grey
white_color = (255, 255, 255)  # RGB values for white
current_color = white_color

# Data rows
for key in notaPorProduto.keys():
    if len(notaPorProduto[key]) < 5:
        notaPorProduto[key].extend([0, 0])

    # Set x position to the calculated starting position for the first column
    pdf.set_x(start_x)

    pdf.set_fill_color(*current_color)

    # First column
    pdf.cell(col_widths[0], 5, txt=str(notaPorProduto[key][0]), border=0, align='C', fill=True)

    # Set x position for the rest of the columns
    pdf.set_x(start_x + col_widths[0])

    for i in range(1, num_columns):
        pdf.cell(col_widths[i], 5, txt=str(notaPorProduto[key][i]), border=0, align='C', fill=True)

    pdf.ln()
    current_color = white_color if current_color == grey_color else grey_color


timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
pdf.output(f"inventario_estoque_{timestamp}.pdf")
conn.close()