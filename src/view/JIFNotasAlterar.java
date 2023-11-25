/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view;

import dao.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import classes.ComboItem;
import classes.ConverterData;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.Timestamp;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import models.Notas;
import models.Produtos;
import static view.JFSistema.JDP;
import static view.JFSistema.fornecedoresOpened;

/**
 *
 * @author wynvern
 */
public class JIFNotasAlterar extends javax.swing.JInternalFrame {  
    
    private void removerEstoqueProdutos(int idProduto, int quantidade) {
        try {
            DataSource dataSource = new DataSource();
            Connection con = dataSource.getConnection();
            PreparedStatement ps = null;
            
            String SQL = "UPDATE produtos SET estoque = estoque - ? WHERE id = ?";
            try {
                ps = con.prepareStatement(SQL);
                ps.setInt(1, quantidade);
                ps.setInt(2, idProduto);
                
                ps.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,"Erro ao salvar!\n"+e);
            }
            
            ps.close();
            dataSource.closeDataSource();
        } catch (SQLException ex) {
            Logger.getLogger(JIFNotasAlterar.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
    
    private void removerNotaAntiga(int idNota){
        int row = table.getSelectedRow();

        DataSource dataSource = new DataSource();
        Connection con = dataSource.getConnection();
        PreparedStatement ps = null;

        try{
            String SQL = "DELETE FROM notas WHERE (id = ?)";

            // para mandar como uma instrução, precisa usar o PreparedStatement
            // traduz o comando SQL para execução
            ps = con.prepareStatement(SQL);
            ps.setInt(1, idNota);

            // executa a inserção no banco
            ps.executeUpdate();
            ps.close();

        }
        catch (SQLException ex){
            //System.err.println("Erro ao salvar os dados "+ex.getMessage());
            JOptionPane.showMessageDialog(null,"Erro ao excluir!\n"+ex);
        }
        finally{
            // fecha o statement e o datasource
            dataSource.closeDataSource();
        }

        DataSource dataSource2 = new DataSource();
        Connection con2 = dataSource2.getConnection();
        PreparedStatement ps2 = null;
        ResultSet rs = null;

        try{
            String SQL = "SELECT * FROM notasItens WHERE (idNota = ?)";

            // para mandar como uma instrução, precisa usar o PreparedStatement
            // traduz o comando SQL para execução
            ps2 = con2.prepareStatement(SQL);
            ps2.setInt(1, idNota);

            // executa a inserção no banco
            rs = ps2.executeQuery();

            while(rs.next()){
                removerEstoqueProdutos(rs.getInt("idProduto"), rs.getInt("quantidade"));
            }

            ps2.close();

        }
        catch (SQLException ex){
            //System.err.println("Erro ao salvar os dados "+ex.getMessage());
            JOptionPane.showMessageDialog(null,"Erro ao excluir!\n"+ex);
        }
        finally{
            // fecha o statement e o datasource
            dataSource.closeDataSource();
        }

        DataSource dataSource3 = new DataSource();
        Connection con3 = dataSource3.getConnection();
        PreparedStatement ps3 = null;
        ResultSet rs2 = null;

        try{
            String SQL = "DELETE FROM notasItens WHERE (idNota = ?)";

            // para mandar como uma instrução, precisa usar o PreparedStatement
            // traduz o comando SQL para execução
            ps3 = con3.prepareStatement(SQL);
            ps3.setInt(1, idNota);

            // executa a inserção no banco
            ps3.executeUpdate();
            ps3.close();

        }
        catch (SQLException ex){
            //System.err.println("Erro ao salvar os dados "+ex.getMessage());
            JOptionPane.showMessageDialog(null,"Erro ao excluir!\n"+ex);
        }
        finally{
            // fecha o statement e o datasource
            dataSource.closeDataSource();
        }
    }
    
    private int notaAntiga = 0;
    
    public void atualizarDadosExistentes(int numeroNotaD, String dataEntradaD, int idFornecedorD, int idNota) {
        numeroNota.setText(String.valueOf(numeroNotaD));
        notaAntiga = idNota;
        dataEntrada.setText(dataEntradaD);
        atualizarListaFornecedores(String.valueOf(idFornecedorD), "");
    
        DataSource dataSource = new DataSource();
        PreparedStatement ps = null;
        try{
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int i = table.getRowCount() - 1; i >= 0; i--) {
                model.removeRow(i);
            }
            
            String SQL = "SELECT * FROM notasItens WHERE idNota = ?";
            
            try {
                // para mandar como uma instrução, precisa usar o PreparedStatement
                // traduz o comando SQL para execução
                ps = dataSource.getConnection().prepareStatement(SQL);
                ps.setInt(1, idNota);
            } catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }

            // executa a consulta no banco
            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
           
            
            // enquanto tiverem registros no ResultSet (rs), 
            // vai adicionando na lista
            while(rs.next()){
                Produtos produtoGet = getProduto(rs.getInt("idProduto"));
                
                model.addRow(new Object[]{produtoGet.getId(), produtoGet.getDescricao(), rs.getInt("quantidade"), rs.getFloat("valor"), Float.parseFloat(rs.getString("quantidade")) * Float.parseFloat(rs.getString("valor"))});
                Float valor = arredondarDuasCasas(Float.parseFloat(total.getText().isEmpty() ? "0.0" : total.getText()) + Float.parseFloat(rs.getString("quantidade")) * Float.parseFloat(rs.getString("valor")));
                total.setText(String.valueOf(valor));
            }
            
            try {
                // fecha o statement e o datasource
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataSource.closeDataSource();
    }   catch (SQLException ex) {
            Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Produtos getProduto(int idPesquisa) {
        DataSource dataSource = new DataSource();
        Produtos result = new Produtos();
        boolean productFound = false;

        try {
            String SQL = "SELECT * FROM produtos WHERE id = ?;";

            try (PreparedStatement ps = dataSource.getConnection().prepareStatement(SQL)) {
                ps.setInt(1, idPesquisa);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        productFound = true;
                        result.setId(rs.getInt("id"));
                        result.setDescricao(rs.getString("descricao"));
                        result.setEstoque(rs.getInt("estoque"));
                        result.setEstoqueMinimo(rs.getInt("estoqueMinimo"));
                        result.setValor(rs.getFloat("valor"));
                        result.setUnidade(rs.getString("unidade"));
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JIFPDV.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (productFound == false) {
            result.setDescricao("<html><font color='red'>Não Encontrado</font></html>");
            result.setId(0);
        }

        return result;
    }
    
    private void salvarDatabase() {
        DataSource dataSource = new DataSource();
        Notas nota = new Notas();
        int rows = table.getRowCount();
        if (rows < 0) {     
            JOptionPane.showMessageDialog(null, "Nenhum produto adicionado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ComboItem selectedComboItem = (ComboItem) fornecedoresBox.getSelectedItem();
        String value = selectedComboItem.getValue();    
        Timestamp timestamp = ConverterData.converterEmData(dataEntrada.getText());
        int generatedId = 0;
        
        nota.setIdEntidade(Integer.parseInt(value));
        nota.setTipo("Entrada");
        nota.setData(timestamp);
        nota.setNumeroNota(numeroNota.getText());
        nota.setPrecoTotal(Float.parseFloat(total.getText()));
        nota.setPendurado(false);

        Connection con = dataSource.getConnection();
        PreparedStatement ps = null;
        try{
            String SQL = "INSERT INTO notas (idEntidade, tipo, data, numeroNota, precoTotal) VALUES (?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(SQL);
            ps.setInt(1,nota.getIdEntidade());
            ps.setString(2,nota.getTipo());
            ps.setTimestamp(3,nota.getData());
            ps.setInt(4, Integer.parseInt(nota.getNumeroNota()));
            ps.setFloat(5, nota.getPrecoTotal());
            
            // executa a inserção no banco
            ps.executeUpdate();
            
            ResultSet rs = ps.executeQuery("SELECT LAST_INSERT_ID()");

            if (rs.next()) {
                generatedId = rs.getInt(1);
            } else {
                // throw an exception from here
            }
            ps.close();
            
        }
        catch (SQLException ex){
            //System.err.println("Erro ao salvar os dados "+ex.getMessage());
            JOptionPane.showMessageDialog(null,"Erro ao salvar!\n"+ex);
        }
        finally{
            // fecha o statement e o datasource
            //ps.close();
            dataSource.closeDataSource();
        }
        
        // ADD EACH ITEM TO notasItens
        
        salvarDatabaseItens(generatedId);
        
    } // Calculate stuff only on the PDF ESTOQUE SUBTRACTS ????
    
    private void salvarDatabaseItens(int idNotaCriada){
        DataSource dataSource = new DataSource();
        Connection con = dataSource.getConnection();
        PreparedStatement ps = null;
        int rows = table.getRowCount();

        String SQL = "INSERT INTO notasItens (idNota, idProduto, valor, quantidade) VALUES (?, ?, ?, ?)";
        try {
            ps = con.prepareStatement(SQL);
            for(int row = 0; row<rows; row++)
            {
                int idProduto = Integer.parseInt(table.getValueAt(row, 0).toString());
                float valor = (float)table.getValueAt(row, 3);
                int quantidadeItem = (int)table.getValueAt(row, 2);
                ps.setInt(1, idNotaCriada);
                ps.setInt(2, idProduto);
                ps.setFloat(3, valor);
                ps.setInt(4, quantidadeItem);

                ps.addBatch();
                
                autializarEstoqueProdutos(idProduto, quantidadeItem);
            }
            ps.executeBatch();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Erro ao salvar!\n"+e);
        }
    }
    
    private void autializarEstoqueProdutos(int idProduto, int quantidade) {
        try {
            DataSource dataSource = new DataSource();
            Connection con = dataSource.getConnection();
            PreparedStatement ps = null;
            
            String SQL = "UPDATE produtos SET estoque = estoque + ? WHERE id = ?";
            try {
                ps = con.prepareStatement(SQL);
                ps.setInt(1, quantidade);
                ps.setInt(2, idProduto);
                
                ps.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,"Erro ao salvar!\n"+e);
            }
            
            ps.close();
            dataSource.closeDataSource();
        } catch (SQLException ex) {
            Logger.getLogger(JIFNotasAlterar.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
    
    private void setItems() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
            int row = table.getSelectedRow();
            if (row < 0) return;
            atualizarListaProdutos(String.valueOf(model.getValueAt(row, 0)), "");
            quantidade.setValue(model.getValueAt(row, 2));
            valorUnitario.setText(String.valueOf(model.getValueAt(row, 3)));
    }
    
    private static float arredondarDuasCasas(float value) {
        // Multiply by 100 to shift two decimal places to the right
        float multipliedValue = value * 100;
        
        // Round to the nearest integer
        long roundedValue = Math.round(multipliedValue);
        
        // Divide by 100 to shift two decimal places to the left
        return (float) roundedValue / 100;
    }
    
    private void atualizarListaProdutos(String searchId, String searchDesc) {
        DataSource dataSource = new DataSource();
        PreparedStatement ps = null;
        try{
            produtosBox.removeAllItems();
            
            
            String SQL = "SELECT * FROM produtos WHERE (id = ? OR ? = '') AND (descricao LIKE ? OR ? LIKE '')";
            
            try {
                ps = dataSource.getConnection().prepareStatement(SQL);
                ps.setString(1, (searchId));
                ps.setString(2, (searchId));
                ps.setString(3, "%" + searchDesc + "%");
                ps.setString(4, searchDesc);
                
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }

            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            while(rs.next()){
                ComboItem comboItem = new ComboItem(rs.getString("descricao") + "   -   Código: " + rs.getString("id"), rs.getString("id"));
                produtosBox.addItem(comboItem);
            }
            
            try {
                // fecha o statement e o datasource
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataSource.closeDataSource();
    }   catch (SQLException ex) {
            Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void atualizarListaFornecedores(String searchId, String searchDesc) {
        DataSource dataSource = new DataSource();
        PreparedStatement ps = null;
        try{
            fornecedoresBox.removeAllItems();
            if (searchId.equals("") && searchDesc.equals("")) {
                ComboItem emptyItem = new ComboItem("Selecione uma opção...", "");
                fornecedoresBox.addItem(emptyItem);
            }
            String SQL = "SELECT * FROM entidades WHERE (id = ? OR ? = '') AND (nome LIKE ? OR ? LIKE '') AND (tipo = ?)";
            
            try {
                ps = dataSource.getConnection().prepareStatement(SQL);
                ps.setString(1, (searchId));
                ps.setString(2, (searchId));
                ps.setString(3, "%" + searchDesc + "%");
                ps.setString(4, searchDesc);
                ps.setString(5, "Fornecedor");
                
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }

            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            boolean foundMatch = false;
            
            while(rs.next()){
                ComboItem comboItem = new ComboItem(rs.getString("nome") + "   -   Código: " + rs.getString("id"), rs.getString("id"));
                fornecedoresBox.addItem(comboItem);
                foundMatch = true;
            }
            
            if (!foundMatch) {
                ComboItem comboItem = new ComboItem("<html><font color='red'>Não Encontrado</font></html>", "");
                fornecedoresBox.addItem(comboItem);
            }
            
            if (!searchId.equals("") || !searchDesc.equals("")) {
                ComboItem emptyItem = new ComboItem("Selecione uma opção...", "");
                fornecedoresBox.addItem(emptyItem);
            }
            
            try {
                // fecha o statement e o datasource
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataSource.closeDataSource();
    }   catch (SQLException ex) {
            Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    /**
     * Creates new form JIFProdutos
     */
    public JIFNotasAlterar() {
        initComponents();
        atualizarListaFornecedores("", "");
        atualizarListaProdutos("", "");
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                setItems();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        valorUnitario = new javax.swing.JTextField();
        produtosBox = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        quantidade = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        numeroNota = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        fornecedoresBox = new javax.swing.JComboBox<>();
        dataEntrada = new javax.swing.JFormattedTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        total = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Adicionar Nota");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Nota.png"))); // NOI18N

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Sair.png"))); // NOI18N
        jToggleButton1.setText("Cancelar");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/produto.png"))); // NOI18N
        jLabel5.setText("Produto:");

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dinheiro.png"))); // NOI18N
        jLabel18.setText("Valor Unitário:");

        valorUnitario.setText("0.0");

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Estoque.png"))); // NOI18N
        jLabel23.setText("Quantidade:");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pesquisar.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fechar.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        quantidade.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(produtosBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valorUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(valorUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(quantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(produtosBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4)
                    .addComponent(jLabel5)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Nota.png"))); // NOI18N
        jLabel19.setText("Número:");

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jLabel20.setText("Data Entrada:");

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Pessoa.png"))); // NOI18N
        jLabel21.setText("Fornecedor:");

        try {
            dataEntrada.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pesquisar.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fechar.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/adicionar.png"))); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numeroNota, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fornecedoresBox, 0, 507, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel19)
                    .addComponent(numeroNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(dataEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(fornecedoresBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dinheiro.png"))); // NOI18N
        jLabel22.setText("Total:");

        total.setEditable(false);
        total.setText("0.0");

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/terminar.png"))); // NOI18N
        jButton7.setText("Finalizar Nota");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(total, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22)
                        .addComponent(jButton7)))
                .addContainerGap())
        );

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Produto", "Quantidade", "Valor Unitário", "Valor Total Item"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/adicionar.png"))); // NOI18N
        jButton5.setText("Adicionar Produto");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/deletar.png"))); // NOI18N
        jButton6.setText("Remover Produto");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/salvar.png"))); // NOI18N
        jButton8.setText("Salvar");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Informações da Nota");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Produto selecionado");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Funções");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jToggleButton1))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        this.dispose();
        JFSistema.notasAlterarOpened = false;
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String valuePesquisa = JOptionPane.showInputDialog(null, "Nome do fornecedor:");
        String idPesquisa = JOptionPane.showInputDialog(null, "Id do fornecedor:");
        
        if (idPesquisa == null) idPesquisa = "";
        if (valuePesquisa == null) valuePesquisa = "";
        if (valuePesquisa.isEmpty()) valuePesquisa = "";
        if (idPesquisa.isEmpty()) idPesquisa = "";
        
        atualizarListaFornecedores(idPesquisa, valuePesquisa);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        atualizarListaFornecedores("", "");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String valuePesquisa = JOptionPane.showInputDialog(null, "Descrição do produto:");
        String idPesquisa = JOptionPane.showInputDialog(null, "Id do produto:");
        
        if (valuePesquisa.isEmpty()) valuePesquisa = "";
        if (idPesquisa.isEmpty()) idPesquisa = "";

        atualizarListaProdutos(idPesquisa, valuePesquisa);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        atualizarListaProdutos("", "");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        ComboItem selectedComboItem = (ComboItem) produtosBox.getSelectedItem();
        String value = selectedComboItem.getValue();
        
        DataSource dataSource = new DataSource();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        PreparedStatement ps = null;
        try{
            String SQL = "SELECT * FROM produtos WHERE id = ?;";
            
            try {
                ps = dataSource.getConnection().prepareStatement(SQL);
                ps.setString(1, value);
                
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }

            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            while(rs.next()){   
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("descricao"), quantidade.getValue(), Float.valueOf(valorUnitario.getText()), Float.parseFloat(valorUnitario.getText()) * Float.parseFloat(quantidade.getValue().toString())});
                Float valor = arredondarDuasCasas(Float.parseFloat(total.getText().isEmpty() ? "0" : total.getText()) + Float.parseFloat(valorUnitario.getText()) * Float.parseFloat(quantidade.getValue().toString()));
                total.setText(String.valueOf(valor));
            }
            
            try {
                // fecha o statement e o datasource
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataSource.closeDataSource();
        }   catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
        int lastRow = table.getRowCount() - 1;
        table.setRowSelectionInterval(lastRow, lastRow);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Nenhuma coluna selecionada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        int unidades = ((Integer) model.getValueAt(row, 2));
        Float preco = ((Float) model.getValueAt(row, 3));
        Float valor = arredondarDuasCasas(Float.parseFloat(total.getText().isEmpty() ? "0" : total.getText()) - preco * unidades);
        total.setText(String.valueOf(valor));

        model.removeRow(row);
        int lastRow = table.getRowCount() - 1;
        if (lastRow < 0) return;
        table.setRowSelectionInterval(lastRow, lastRow);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Nenhuma coluna selecionada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ComboItem selectedComboItem = (ComboItem) produtosBox.getSelectedItem();
        String value = selectedComboItem.getValue();
        String produtoName = "";

        DataSource dataSource = new DataSource();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        PreparedStatement ps = null;
        
        
        Float valorAntigo = Float.parseFloat(model.getValueAt(row, 3).toString());
        int quantidadeAntigo = Integer.parseInt(model.getValueAt(row, 2).toString());
        
        Float valor = arredondarDuasCasas(Float.parseFloat(total.getText().isEmpty() ? "0" : total.getText()) - valorAntigo * quantidadeAntigo);
        total.setText(String.valueOf(valor));
        
        
        try{
            String SQL = "SELECT * FROM produtos WHERE id = ?;";
            
            try {
                ps = dataSource.getConnection().prepareStatement(SQL);
                ps.setString(1, value);
                
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }

            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            while(rs.next()){   
                produtoName = rs.getString("descricao");
            }
            
            try {
                // fecha o statement e o datasource
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataSource.closeDataSource();
        }   catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        model.setValueAt(produtoName, row, 1);
        model.setValueAt(value, row, 0);
        model.setValueAt(Integer.parseInt(quantidade.getValue().toString()), row, 2);
        model.setValueAt(Float.parseFloat(valorUnitario.getText()), row, 3);
        
        Float valor2 = Float.parseFloat(valorUnitario.getText()) * Float.parseFloat(quantidade.getValue().toString());
        
        model.setValueAt(valor2, row, 4);
        
        valor = arredondarDuasCasas(valor + valor2);
        total.setText(String.valueOf(valor));
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        try {
            Integer.parseInt(numeroNota.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Número da nota digitada incorretamente.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        ComboItem selectedComboItem = (ComboItem) fornecedoresBox.getSelectedItem();
        String value = selectedComboItem.getValue();    
        
        if (value.equals("")) {
            JOptionPane.showMessageDialog(null, "Nenhum fornecedor escolhido.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (!dataEntrada.isEditValid()) {
            JOptionPane.showMessageDialog(null, "A data fornecida é incorreta.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int lastRow = table.getRowCount() - 1;
        if (lastRow < 0) {
            JOptionPane.showMessageDialog(null, "Nenhum produto adicionado na nota.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int opcao = JOptionPane.showConfirmDialog(null,"Deseja realmente concluir e salvar a nota?","Confirmação",JOptionPane.YES_OPTION);
        if(opcao == JOptionPane.YES_OPTION) {
            if (notaAntiga != 0) removerNotaAntiga(notaAntiga);
            salvarDatabase();
            JOptionPane.showMessageDialog(null, "Nota salva com sucesso.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            JFSistema.notasAlterarOpened = false;
            this.dispose();
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        if (!fornecedoresOpened) {
            JIFClientes janela = new JIFClientes();
            JDP.add(janela);
            try {
                janela.setMaximum(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(JFSistema.class.getName()).log(Level.SEVERE, null, ex);
            }
            janela.setVisible(true);
            fornecedoresOpened = true;
        } else {
            JFSistema.bringInternalFrameToFront(JDP, "Cadastro de Clientes");
        }

        JInternalFrame frame = JFSistema.getInternalFrameByTitle("Cadastro de Clientes");
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                // Code to execute when the internal frame is closed
                atualizarListaFornecedores("", "");
            }
        });
    }//GEN-LAST:event_jButton9ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField dataEntrada;
    private javax.swing.JComboBox<ComboItem> fornecedoresBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JTextField numeroNota;
    private javax.swing.JComboBox<ComboItem> produtosBox;
    private javax.swing.JSpinner quantidade;
    private javax.swing.JTable table;
    private javax.swing.JTextField total;
    private javax.swing.JTextField valorUnitario;
    // End of variables declaration//GEN-END:variables
}
