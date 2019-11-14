package pe.gob.osce.rnp.seg.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pe.gob.osce.rnp.seg.model.dto.TokenDTO;

import java.util.Optional;

@Controller
@RequestMapping("/externaluserauth")
public class SunatSsoController {

    private static final String VIEW_FINAL_SUNAT_SSO = "sunatsso";
    private static final String SCHEMA_HASH = "its_sunat_sso";
    private static final String OAUTH_GRANT_TYPE_FLOW = "authorization_code";
    private static final String OAUTH_SCOPE_RNP = "https://eap.osce.gob.pe";
    public static final Logger LOGGER = LogManager.getLogger(SunatSsoController.class);

    @Value("${oauth.client.id}")
    private String oauthClientId;

    @Value("${oauth.client.secret}")
    private String oauthSecret;

    @Value("${oauth.endpoint.get.token}")
    private String oauthEndpointToken;

    @Value("${angular.endpoint.bridge}")
    private String angularEndpoint;

    @Value("${oauth.endpoint.post.sso.success}")
    private String endpointPostSsoSuccess;

    @Value("${oauth.sunat.client.id}")
    private String oauthSunatClientId;

    @Value("${oauth.sunat.client.secret}")
    private String oauthSunatSecret;

    @Value("${oauth.sunat.endpoint.get.token}")
    private String oauthSunatEndpointToken;

    @GetMapping("")
    public @ResponseBody String intercambiarTokenPostLoginEnOauthSunat(@RequestParam(value = "code") String codigoParaCanjear){
        String endpointGetTokenFromSunat = String.format(oauthSunatEndpointToken, oauthSunatClientId);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map =
                new LinkedMultiValueMap<>();
        map.add("grant_type", OAUTH_GRANT_TYPE_FLOW);
        map.add("code", codigoParaCanjear);
        map.add("client_id", oauthSunatClientId);
        map.add("client_secret", oauthSunatSecret);
        map.add("scope", OAUTH_SCOPE_RNP);

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(map, headers);

        try {
            ResponseEntity<TokenDTO> secResponseEntity = restTemplate.exchange(endpointGetTokenFromSunat, HttpMethod.POST, entity, TokenDTO.class);
            if(secResponseEntity.getStatusCode() == HttpStatus.OK){
                LOGGER.info(secResponseEntity.getBody().toString());
                Optional<TokenDTO> optTokenDto = Optional.ofNullable(secResponseEntity.getBody());
                if(optTokenDto.isPresent()){
                    return optTokenDto.get().getAccess_token();
                }
            }
        }catch (HttpClientErrorException ex){
            ex.printStackTrace();
            LOGGER.info(ex.getLocalizedMessage());
        } catch (Exception ex){
            ex.printStackTrace();
            LOGGER.info(ex.getLocalizedMessage());
        }
        return "Failed...check the console";
    }


    /*

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        LOGGER.info("SUNAT SSO CONTROLLER: handleRequestInternal*******************************");
        ModelAndView model = new ModelAndView(VIEW_FINAL_SUNAT_SSO);
        String rucAutenticado = obtenerRucDecodifcado();
        if(rucAutenticado.equals("0"))
           return model;
        String oauthToken = obtenerToken();
        if(oauthToken.equals("0"))
           return model;
        //Borrando el objeto en session
        session.removeAttribute("usuarioBean");

        //Invocando el endpoint para registrar ruc que va actualizar su correo(Como método de validación y respaldo de seguridad)
        //registrarFlagPreviaActualizacionCorreo(rucAutenticado, httpServletRequest.getRemoteAddr(), oauthToken);
        //Encodificando
        Long time = new Date().getTime();
        String rucHash = Parseador.getEncodeHashLong32Id(SCHEMA_HASH, Long.valueOf(rucAutenticado));
        String timeHash = Parseador.getEncodeHashLong32Id(SCHEMA_HASH, time);
        model.addObject("itscur", rucHash);
        model.addObject("itsemitxam", timeHash);
        model.addObject("itslru", angularEndpoint);
        model.addObject("itspi", new String(
                Base64.getEncoder().encode(
                        httpServletRequest.getRemoteAddr().getBytes()),
                StandardCharsets.UTF_8).replaceAll("\\=", ""));
        return model;
    }

    private String obtenerRucDecodifcado(){
        Optional<Object> optUser = Optional.ofNullable(session.getAttribute("usuarioBean"));
        if(!optUser.isPresent()){
            return "0";
        }
        return ((UsuarioBean) optUser.get()).getNumRUC();
    }

    private String obtenerToken(){
        String authVisa = oauthClientId + ":" + oauthSecret;
        String endpointOauthToken = oauthEndpointToken;
        byte[] encodedAuth = org.apache.commons.codec.binary.Base64.encodeBase64(authVisa.getBytes(StandardCharsets.US_ASCII));

        String authHeader = "Basic " + new String( encodedAuth );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authHeader);

        MultiValueMap<String, String> map =
                new LinkedMultiValueMap<>();
        map.add("grant_type","client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(map, headers);

        try {
            ResponseEntity<TokenDTO> secResponseEntity = restTemplate.exchange(endpointOauthToken, HttpMethod.POST, entity, TokenDTO.class);
            if(secResponseEntity.getStatusCode() == HttpStatus.OK){
                LOGGER.info(secResponseEntity.getBody().toString());
                System.out.println(secResponseEntity.getBody().toString());
                Optional<String> optTokenDto = Optional.ofNullable(secResponseEntity.getBody().getAccess_token());
                if(optTokenDto.isPresent()){
                    return optTokenDto.get();
                }
            }
        }catch (HttpClientErrorException ex){
            LOGGER.info(ex.getLocalizedMessage());
        } catch (Exception ex){
            LOGGER.info(ex.getLocalizedMessage());
        }
        return "0";
    }

    private String registrarFlagPreviaActualizacionCorreo(String ruc, String ipCliente, String token){
        String endpointPostSso = endpointPostSsoSuccess;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer "+token);

        MultiValueMap<String, String> map =
                new LinkedMultiValueMap<>();
        map.add("ipCliente", ipCliente);
        map.add("ruc",ruc);

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Respuesta> secResponseEntity = restTemplate.exchange(endpointPostSso, HttpMethod.POST, entity, Respuesta.class);
            if(secResponseEntity.getStatusCode() == HttpStatus.OK){
                LOGGER.info(secResponseEntity.getBody().toString());
                Optional<Respuesta> optTokenDto = Optional.ofNullable(secResponseEntity.getBody());
                if(optTokenDto.isPresent()){
                    return optTokenDto.get().getResponseCode()+"";
                }
            }
        }catch (HttpClientErrorException ex){
            LOGGER.info(ex.getLocalizedMessage());
        } catch (Exception ex){
            LOGGER.info(ex.getLocalizedMessage());
        }
        return "0";
    }*/
}
