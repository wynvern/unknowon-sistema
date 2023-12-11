#pip install mysql-connector-python
import mysql.connector
from datetime import datetime
from fpdf import FPDF
import os
import configparser

config = configparser.ConfigParser()
config.read('config.ini')

conn = mysql.connector.connect(
    host = config.get('mysql', 'hostname'),
    user = config.get('mysql', 'username'),
    password = config.get('mysql', 'password'),
    database = config.get('mysql', 'database')
)

cursor = conn.cursor()

cursor.execute(f"SELECT * FROM produtos;")
produtos = cursor.fetchall()

produtosAviso = []


for produto in produtos:
    # Creates a dict key for each product
    if (produto[4] <= produto[5]): #validation
        produtoC = {}
        produtoC['id'] = produto[0]
        produtoC['descricao'] = produto[2]
        produtoC['valor'] =  produto[3]
        produtoC['estoque'] =  produto[4]
        produtoC['estoqueMinimo'] =  produto[5]
        produtoC['unidade'] =  produto[6]
        produtosAviso.append(produtoC)


pdf = FPDF()
pdf.add_page()
pdf.set_font("Arial", size=9)

current_directory = os.getcwd()
relative_path = os.path.join('src', 'images', 'banner.png')
full_path = os.path.abspath(os.path.join(current_directory, relative_path))
pdf.image(full_path, x=10, y=8, w=30)

pdf.set_font("Arial", style="B", size=16)  # Adjust the size as needed

# Add title to the header
pdf.cell(0, 10, 'Relatório de Compras', ln=True, align='C')

# Set font for the date
pdf.set_font("Arial", size=9)

current_date = datetime.now().strftime("%d/%m/%Y")
pdf.cell(0, 10, f'Data Gerado: {current_date}', ln=True, align='C')

# Calculate total width available for columns
total_width = pdf.w - 2 * pdf.l_margin
num_columns = 6  # Assuming 6 columns in your table

# Header row
header_labels = ["Código", "Descrição", "Valor Un", "Unidade", "Estoque Min", "Estoque"]

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


header_labels_correct = ["id", "descricao", "valor", "unidade", "estoqueMinimo", "estoque"]

# Data rows
for key in produtosAviso:
    # Set x position to the calculated starting position for the first column
    pdf.set_x(start_x)

    pdf.set_fill_color(*current_color)


    # Loop through columns
    for i in range(num_columns):
        if (i == 4 or i == 5): pdf.set_text_color(255, 0, 0)
        pdf.cell(col_widths[i], 5, txt=str(key[header_labels_correct[i]]), border=0, align='C', fill=True)
        pdf.set_text_color(0, 0, 0)

    pdf.ln()
    current_color = white_color if current_color == grey_color else grey_color



timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
pdf.output(f"relatório_compras_{timestamp}.pdf")
conn.close()