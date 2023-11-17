/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view;

import classes.ComboItem;
import dao.DataSource;
import static java.lang.System.currentTimeMillis;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Notas;
import java.sql.Timestamp;

/**
 *
 * @author wynvern
 */
public class JIFVender extends javax.swing.JInternalFrame {
    
    private void salvarDatabase(boolean isPendurado) {
        DataSource dataSource = new DataSource();
        Notas nota = new Notas();
        int rows = table.getRowCount();
        if (rows < 0) {     
            JOptionPane.showMessageDialog(null, "Nenhum produto adicionado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ComboItem selectedComboItem = (ComboItem) clientesBox.getSelectedItem();
        String value = selectedComboItem.getValue();    
        Timestamp timestamp = new Timestamp(currentTimeMillis()) {};
        int generatedId = 0;
        
        nota.setIdEntidade(Integer.parseInt(value));
        nota.setTipo("Saida");
        nota.setData(timestamp);
        nota.setPendurado(isPendurado);

        Connection con = dataSource.getConnection();
        PreparedStatement ps = null;
        try{
            String SQL = "INSERT INTO notas (idEntidade, tipo, data, pendurado, precoTotal) VALUES (?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(SQL);
            ps.setInt(1,nota.getIdEntidade());
            ps.setString(2,nota.getTipo());
            ps.setTimestamp(3,nota.getData());
            ps.setBoolean(4,nota.isPendurado());
            ps.setFloat(5, Float.parseFloat(total.getText()));

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
    
    private void autializarEstoqueProdutos(int idProduto, int quantidade) {
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
                int idProduto = (int)table.getValueAt(row, 0);
                float valor = (float)table.getValueAt(row, 3);
                int quantidadeItem = (int)table.getValueAt(row, 2);
                ps.setInt(1, idNotaCriada);
                ps.setInt(2, idProduto);
                ps.setFloat(3, valor);
                ps.setInt(4, quantidadeItem);
                autializarEstoqueProdutos(idProduto, quantidadeItem);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Erro ao salvar!\n"+e);
        }
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
    
    private void atualizarListaClientes(String searchId, String searchDesc) {
        DataSource dataSource = new DataSource();
        PreparedStatement ps = null;
        try{
            clientesBox.removeAllItems();
            String SQL = "SELECT * FROM entidades WHERE (id = ? OR ? = '') AND (nome LIKE ? OR ? LIKE '') AND (tipo = ?)";
            
            try {
                ps = dataSource.getConnection().prepareStatement(SQL);
                ps.setString(1, (searchId));
                ps.setString(2, (searchId));
                ps.setString(3, "%" + searchDesc + "%");
                ps.setString(4, searchDesc);
                ps.setString(5, "Cliente");
                
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
                ComboItem comboItem = new ComboItem(rs.getString("nome") + "   -   Código: " + rs.getString("id"), rs.getString("id"));
                clientesBox.addItem(comboItem);
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
    public JIFVender() {
        initComponents();
        atualizarListaProdutos("", "");
        atualizarListaClientes("", "");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        total = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        produtosBox = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        quantidade = new javax.swing.JSpinner();
        jButton8 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        clientesBox = new javax.swing.JComboBox<>();
        jButton7 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Vender");
        setToolTipText("");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vender.png"))); // NOI18N

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Sair.png"))); // NOI18N
        jButton3.setText("Cancelar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dinheiro.png"))); // NOI18N
        jLabel4.setText("Preço Total:");

        total.setText("0.00");
        total.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(223, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(total)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/produto.png"))); // NOI18N
        jLabel1.setText("Produto:");

        produtosBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                produtosBoxActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/adicionar.png"))); // NOI18N
        jButton1.setText("Adicionar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/peso.png"))); // NOI18N
        jLabel2.setText("Quantidade:");

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fechar.png"))); // NOI18N
        jButton2.setText("Remover");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pesquisar.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        quantidade.setModel(new javax.swing.SpinnerNumberModel(1, 1, 99, 1));

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/naoPesquisa.png"))); // NOI18N
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(produtosBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(quantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8)
                    .addComponent(produtosBox, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(quantidade)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Pessoa.png"))); // NOI18N
        jLabel3.setText("Cliente:");

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pesquisar.png"))); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/naoPesquisa.png"))); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clientesBox, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jButton7)
                            .addComponent(jLabel3)
                            .addComponent(jButton9))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(clientesBox, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vender.png"))); // NOI18N
        jButton4.setText("Vender");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cartao.png"))); // NOI18N
        jButton5.setText("Crediário");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap())
        );

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descrição", "Quantidade", "Preço"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.dispose();
        JFSistema.venderOpened = false;
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String valuePesquisa = JOptionPane.showInputDialog(null, "Descrição do produto:");
        String idPesquisa = JOptionPane.showInputDialog(null, "Id do produto:");
        
        if (valuePesquisa.isEmpty()) valuePesquisa = "";
        if (idPesquisa.isEmpty()) idPesquisa = "";
        
        System.out.println(idPesquisa);
        System.out.println(valuePesquisa);

        atualizarListaProdutos(idPesquisa, valuePesquisa);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        atualizarListaProdutos("", "");
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
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
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("descricao"), quantidade.getValue(), rs.getFloat("valor")});
                Float valor = arredondarDuasCasas(Float.parseFloat(total.getText().isEmpty() ? "0" : total.getText()) + rs.getFloat("valor") * Float.parseFloat(quantidade.getValue().toString()));
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
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
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
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String valuePesquisa = JOptionPane.showInputDialog(null, "Nome do cliente:");
        String idPesquisa = JOptionPane.showInputDialog(null, "Id do cliente:");
        
        if (valuePesquisa.isEmpty()) valuePesquisa = "";
        if (idPesquisa.isEmpty()) idPesquisa = "";
        
        System.out.println(idPesquisa);
        System.out.println(valuePesquisa);

        atualizarListaClientes(idPesquisa, valuePesquisa);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        atualizarListaClientes("", "");
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int opcao = JOptionPane.showConfirmDialog(null,"Deseja realmente pendurar a conta?","Confirmação",JOptionPane.YES_OPTION);
        if(opcao == JOptionPane.YES_OPTION) {
            salvarDatabase(true);
            JOptionPane.showMessageDialog(null, "Venda pendurada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            JFSistema.venderOpened = false;
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int opcao = JOptionPane.showConfirmDialog(null,"Deseja realmente concluir a conta?","Confirmação",JOptionPane.YES_OPTION);
        if(opcao == JOptionPane.YES_OPTION) {
            salvarDatabase(false);
            JOptionPane.showMessageDialog(null, "Venda finalizada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            JFSistema.venderOpened = false;
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void produtosBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_produtosBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_produtosBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ComboItem> clientesBox;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<ComboItem> produtosBox;
    private javax.swing.JSpinner quantidade;
    private javax.swing.JTable table;
    private javax.swing.JTextField total;
    // End of variables declaration//GEN-END:variables
}
