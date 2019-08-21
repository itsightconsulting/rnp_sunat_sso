package pe.gob.osce.rnp.seg.utils;

import org.hashids.Hashids;

public class Parseador {

    private Parseador(){}

    public static String getEncodeHashLong32Id(String schema,  Long num){
        Hashids rfIdHash = new Hashids(schema, 32);
        return rfIdHash.encode(num);
    }
}
