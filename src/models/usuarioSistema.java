package models;


import com.mysql.cj.jdbc.Blob;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author wynvern
 */
public class usuarioSistema {
    private int id;
    private String nome;
    private String senha;
    private String telefone;
    private String email;
    private String permissao;
    private byte[] imagemPerfil;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the senha
     */
    public String getSenha() {
        return senha;
    }

    /**
     * @param senha the senha to set
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * @return the telefone
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * @param telefone the telefone to set
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the permissao
     */
    public String getPermissao() {
        return permissao;
    }

    /**
     * @param permissao the permissao to set
     */
    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }

    /**
     * @return the imagemPerfil
     */
    public byte[] getImagemPerfil() {
        return imagemPerfil;
    }

    /**
     * @param imagemPerfil the imagemPerfil to set
     */
    public void setImagemPerfil(byte[] imagemPerfil) {
        this.imagemPerfil = imagemPerfil;
    }
}
