package pe.gob.osce.rnp.seg.utils;

import org.hashids.Hashids;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Parseador {

    private Parseador(){}

    public static String getEncodeHashLong32Id(String schema,  Long num){
        Hashids rfIdHash = new Hashids(schema, 32);
        return rfIdHash.encode(num);
    }

    public static String encodeIpAddress(String remoteAddress){
        return new String(
                Base64.getEncoder().encode(remoteAddress.getBytes()),
                StandardCharsets.UTF_8).replaceAll("\\=", "");
    }
}
