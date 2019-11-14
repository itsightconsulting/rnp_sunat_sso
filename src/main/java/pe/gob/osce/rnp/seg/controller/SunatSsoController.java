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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import pe.gob.osce.rnp.seg.cfg.SSLClientFactory;
import pe.gob.osce.rnp.seg.model.dto.TokenDTO;
import pe.gob.osce.rnp.seg.utils.Parseador;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/externaluserauth")
public class SunatSsoController {

    private static final String VIEW_FINAL_SUNAT_SSO = "sunatsso";
    private static final String SCHEMA_HASH = "its_sunat_sso";
    private static final String OAUTH_GRANT_TYPE_FLOW = "authorization_code";
    private static final String OAUTH_SCOPE_RNP = "https://eap.osce.gob.pe";
    public static final Logger LOGGER = LogManager.getLogger(SunatSsoController.class);

    @Value("${angular.endpoint.bridge}")
    private String angularEndpoint;

    @Value("${oauth.sunat.client.id}")
    private String oauthSunatClientId;

    @Value("${oauth.sunat.client.secret}")
    private String oauthSunatSecret;

    @Value("${oauth.sunat.endpoint.get.token}")
    private String oauthSunatEndpointToken;

    @GetMapping("")
    public ModelAndView intercambiarTokenPostLoginEnOauthSunat(@RequestParam(value = "code") String codigoParaCanjear, HttpServletRequest request){
        LOGGER.info("intercambiarTokenPostLoginEnOauthSunat(...) method called.");
        ModelAndView model = new ModelAndView(VIEW_FINAL_SUNAT_SSO);

        String endpointGetTokenFromSunat = String.format(oauthSunatEndpointToken, oauthSunatClientId);

        RestTemplate restTemplate = new RestTemplate(SSLClientFactory.getClientHttpRequestFactory(SSLClientFactory.HttpClientType.HttpClient));
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
                    TokenDTO objToken = optTokenDto.get();
                    //Encodificando
                    Long time = new Date().getTime();
                    String timeHash = Parseador.getEncodeHashLong32Id(SCHEMA_HASH, time);
                    model.addObject("itsemitxam", timeHash);
                    model.addObject("itslru", angularEndpoint);
                    model.addObject("itspi", Parseador.encodeIpAddress(request.getRemoteAddr()));
                    model.addObject("itsnekottanus", objToken.getAccess_token());
                    return model;
                }
            }
        }catch (HttpClientErrorException ex){
            ex.printStackTrace();
            LOGGER.info(ex.getLocalizedMessage());
        } catch (Exception ex){
            ex.printStackTrace();
            LOGGER.info(ex.getLocalizedMessage());
        }
        return model;
    }
}
