package com.twt.appcobranzas.model;

public class RowCliente {
    private String empresa;
    private String cliente;
    private String n_cliente;
    private String n_clienteaux;
    private String direccion;
    private String ciudad;
    private String provincia;
    private String ruc;
    private String telefono;
    private String vendedor;
    private double descuento;
    private String forma_pago;
    private String mail1;
    private String mail2;
    private String persona;


    public RowCliente(String empresa, String cliente, String n_cliente, String n_clienteaux, String direccion, String ciudad, String provincia, String ruc, String telefono, String vendedor,
                      double descuento, String forma_pago, String mail1, String mail2, String persona){
        this.setEmpresa(empresa);
        this.setCliente(cliente);
        this.setN_cliente(n_cliente);
        this.setN_clienteaux(n_clienteaux);
        this.setDireccion(direccion);
        this.setCiudad(ciudad);
        this.setProvincia(provincia);
        this.setRuc(ruc);
        this.setTelefono(telefono);
        this.setVendedor(vendedor);
        this.setDescuento(descuento);
        this.setForma_pago(forma_pago);
        this.setMail1(mail1);
        this.setMail2(mail2);
        this.setPersona(persona);
    }
    
    public RowCliente() {
        super();
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getN_cliente() {
        return n_cliente;
    }

    public void setN_cliente(String n_cliente) {
        this.n_cliente = n_cliente;
    }

    public String getN_clienteaux() {
        return n_clienteaux;
    }

    public void setN_clienteaux(String n_clienteaux) {
        this.n_clienteaux = n_clienteaux;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getForma_pago() {
        return forma_pago;
    }

    public void setForma_pago(String forma_pago) {
        this.forma_pago = forma_pago;
    }

    public String getMail1() {
        return mail1;
    }

    public void setMail1(String mail1) {
        this.mail1 = mail1;
    }

    public String getMail2() {
        return mail2;
    }

    public void setMail2(String mail2) {
        this.mail2 = mail2;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}
