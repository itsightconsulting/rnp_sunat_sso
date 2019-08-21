package pe.gob.osce.rnp.seg.model;

import java.io.Serializable;

public class Respuesta implements Serializable {

    /// <summary>
    /// Codigo de la petición
    /// </summary>
    private int responseCode;

    /// <summary>
    /// Booleano sobre el éxito de la petición de datos solicitada
    /// </summary>
    private boolean flag;

    /// <summary>
    /// Objeto que contiene toda la data
    /// </summary>
    private String d;

    public Respuesta(){}

    public Respuesta(int responseCode, int intFlag) {
        this.responseCode = responseCode;
        this.flag = intFlag > 0;
        this.d = null;
    }

    public Respuesta(int responseCode, int intFlag, String d) {
        this.responseCode = responseCode;
        this.flag = intFlag > 0;
        this.d = d;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    @Override
    public String toString() {
        return "Respuesta{" +
                "responseCode=" + responseCode +
                ", flag=" + flag +
                ", d='" + d + '\'' +
                '}';
    }
}

