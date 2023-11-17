/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view;

import dao.DataSource;
import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import models.Notas;
import static view.JFSistema.JDP;
import static view.JFSistema.notasAlterarOpened;
import java.sql.Timestamp;
import classes.ComboItem;
import static classes.ConverterData.converterEmData;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import javax.swing.JOptionPane;
import models.Entidades;

/**
 *
 * @author wynvern
 */
public class JIFNotas extends javax.swing.JInternalFrame {

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
                ps.setString(4, "%" + searchDesc + "%");
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
            
            while(rs.next()){
                ComboItem comboItem = new ComboItem(rs.getString("nome") + "   -   Código: " + rs.getString("id"), rs.getString("id"));
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
    
    private Entidades getEntidade(int idPesquisa) {
        DataSource dataSource = new DataSource();
        Entidades result = null;

        try {
            String SQL = "SELECT * FROM entidades WHERE id = ?;";

            try (PreparedStatement ps = dataSource.getConnection().prepareStatement(SQL)) {
                ps.setInt(1, idPesquisa);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result = new Entidades();
                        result.setId(rs.getInt("id"));
                        result.setNome(rs.getString("nome"));
                        result.setTipo(rs.getString("tipo"));
                        result.setTelefone(rs.getString("telefone"));
                        result.setEmail(rs.getString("email"));
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JIFPDV.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }    

    private void atualizarTabela(String idPesquisa, Timestamp dataPesquisa, String fornecedorId) {
        DataSource dataSource = new DataSource();
        PreparedStatement ps = null;
        try{
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int i = table.getRowCount() - 1; i >= 0; i--) {
                model.removeRow(i);
            }
            
            StringBuilder SQLBuilder = new StringBuilder("SELECT * FROM notas WHERE (id = ? OR ? = '') AND (idEntidade = ? OR ? = '') AND tipo = ?");
            
            if (dataPesquisa != null) {
                SQLBuilder.append(" AND (DATE(data) = DATE(?))");
            } 

            try {
                ps = dataSource.getConnection().prepareStatement(SQLBuilder.toString());
                ps.setString(1, idPesquisa);
                ps.setString(2, idPesquisa);
                ps.setString(3, fornecedorId);
                ps.setString(4, fornecedorId);
                ps.setString(5, "Entrada");
                
                if (dataPesquisa != null) {
                    ps.setTimestamp(6, dataPesquisa);
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(JIFNotas.class.getName()).log(Level.SEVERE, null, ex);
            }

            // executa a consulta no banco
            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(JIFNotas.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            while(rs.next()){

                Entidades entidadeTabela = getEntidade(rs.getInt("idEntidade"));
                model.addRow(new Object[]{rs.getInt("id"), new ComboItem(entidadeTabela.getNome(), String.valueOf(entidadeTabela.getId())), rs.getString("data"), rs.getString("numeroNota"), rs.getString("precoTotal")});
            }
            
            try {
                // fecha o statement e o datasource
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(JIFNotas.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataSource.closeDataSource();
    }   catch (SQLException ex) {
            Logger.getLogger(JIFNotas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Creates new form JIFProdutos
     */
    public JIFNotas() {
        initComponents();
        atualizarTabela("", null, "");
        atualizarListaFornecedores("", "");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        idPesquisa = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        dataPesquisa = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        fornecedoresBox = new javax.swing.JComboBox<>();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        jToggleButton4 = new javax.swing.JToggleButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Notas de Entrada");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Nota.png"))); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Nota.png"))); // NOI18N
        jLabel1.setText("Nota:");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jLabel2.setText("Data:");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pesquisar.png"))); // NOI18N
        jButton1.setText("Pesquisar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/naoPesquisa.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        try {
            dataPesquisa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        dataPesquisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataPesquisaActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Pessoa.png"))); // NOI18N
        jLabel3.setText("Fornecedor:");

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pesquisar.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/naoPesquisa.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fornecedoresBox, 0, 319, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fornecedoresBox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jButton1)
                        .addComponent(jButton2)
                        .addComponent(jLabel3)
                        .addComponent(jButton4)
                        .addComponent(jButton5)
                        .addComponent(dataPesquisa, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                    .addComponent(idPesquisa, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Sair.png"))); // NOI18N
        jToggleButton1.setText("Sair");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jToggleButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/adicionar.png"))); // NOI18N
        jToggleButton2.setText("Adicionar");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        jToggleButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/deletar.png"))); // NOI18N
        jToggleButton3.setText("Deletar");
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton3ActionPerformed(evt);
            }
        });

        jToggleButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/editar.png"))); // NOI18N
        jToggleButton4.setText("Editar");
        jToggleButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton4ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/recarregar.png"))); // NOI18N
        jButton3.setText("Atualizar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToggleButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton2)
                    .addComponent(jToggleButton3)
                    .addComponent(jToggleButton4)
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Código", "Fornecedor", "Data", "Número da Nota", "Preço Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jToggleButton1))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        this.dispose(); 
        JFSistema.notasEntradaOpened = false;
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        if (!notasAlterarOpened) {
            JIFNotasAlterar janela = new JIFNotasAlterar();

            JDP.add(janela);
            try {
                janela.setMaximum(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(JFSistema.class.getName()).log(Level.SEVERE, null, ex);
            }
            janela.setVisible(true);
            notasAlterarOpened = true;
        }
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (dataPesquisa.getText().length() < 6 && !dataPesquisa.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Data de pesquisa digitada inválida.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            if (!idPesquisa.getText().equals("")) Integer.parseInt(idPesquisa.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Valor digitado em nota inválido.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Timestamp dataD = null;
        if (!dataPesquisa.getText().equals("")) {
            dataD = converterEmData(dataPesquisa.getText());
        }
        
        ComboItem selectedComboItem = (ComboItem) fornecedoresBox.getSelectedItem();
        String value = selectedComboItem.getValue();

        atualizarTabela(idPesquisa.getText(), dataD, value);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        String valuePesquisa = JOptionPane.showInputDialog(null, "Nome do fornecedor:");
        String idPesquisa = JOptionPane.showInputDialog(null, "Id do fornecedor:");
        
        if (valuePesquisa.isEmpty()) valuePesquisa = "";
        if (idPesquisa.isEmpty()) idPesquisa = "";
        
        atualizarListaFornecedores(idPesquisa, valuePesquisa);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        atualizarListaFornecedores("", "");
    }//GEN-LAST:event_jButton5ActionPerformed

    private void dataPesquisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataPesquisaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dataPesquisaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        atualizarTabela("", null, "");
        atualizarListaFornecedores("", "");
        dataPesquisa.setValue("");
        idPesquisa.setText("");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        atualizarTabela("", null, "");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Nenhuma coluna selecionada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        };
        
        int opcao = JOptionPane.showConfirmDialog(null,"Deseja realmente excluír o valor com o código "+ table.getValueAt(row, 0).toString() + "?","Exclusão",JOptionPane.YES_OPTION);
        if(opcao == JOptionPane.YES_OPTION) {
            DataSource dataSource = new DataSource();
            Connection con = dataSource.getConnection();
            PreparedStatement ps = null;

            try{
                String SQL = "DELETE FROM notas WHERE (id = ?)";

                // para mandar como uma instrução, precisa usar o PreparedStatement
                // traduz o comando SQL para execução
                ps = con.prepareStatement(SQL);
                ps.setInt(1, Integer.parseInt(table.getValueAt(row, 0).toString()));

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
                ps2.setInt(1, Integer.parseInt(table.getValueAt(row, 0).toString()));

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
                ps3.setInt(1, Integer.parseInt(table.getValueAt(row, 0).toString()));

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

        atualizarTabela("", null, "");
        int lastRow = table.getRowCount() - 1;
        if (lastRow < 0) return;
        table.setRowSelectionInterval(lastRow, lastRow);
    }//GEN-LAST:event_jToggleButton3ActionPerformed

    private void jToggleButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton4ActionPerformed
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Nenhuma coluna selecionada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (!notasAlterarOpened) {
            JIFNotasAlterar janela = new JIFNotasAlterar();

            JDP.add(janela);
            try {
                janela.setMaximum(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(JFSistema.class.getName()).log(Level.SEVERE, null, ex);
            }

            int numeroDaNota = Integer.parseInt(table.getValueAt(row, 3).toString());
            String dataEntrada = table.getValueAt(row, 2).toString();          
            ComboItem selectedComboItem = (ComboItem) table.getValueAt(row, 1);
            int idFornecedor = Integer.parseInt(selectedComboItem.getValue()); 
            
            int codigoNota = Integer.parseInt(table.getValueAt(row, 0).toString());
            janela.atualizarDadosExistentes(numeroDaNota, dataEntrada, idFornecedor, codigoNota);
            janela.setVisible(true);
            notasAlterarOpened = true;
        }
    }//GEN-LAST:event_jToggleButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField dataPesquisa;
    private javax.swing.JComboBox<ComboItem> fornecedoresBox;
    private javax.swing.JTextField idPesquisa;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToggleButton jToggleButton4;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
