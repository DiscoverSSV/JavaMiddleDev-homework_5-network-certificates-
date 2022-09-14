package ru.digitalhabits.homework5.component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MetricsSSLCertificate {

    private String name_metric = "tomcat.ssl.certificate";

    public MetricsSSLCertificate(MeterRegistry meterRegistry, ApplicationContext context, TomcatServletWebServerFactory tomcatServletWebServerFactory) {
        try {
            Ssl ssl = tomcatServletWebServerFactory.getSsl();
            if (ssl!=null) {
                X509Certificate certificate = getCertificate(ssl, context);

                meterRegistration(meterRegistry, generateMap(certificate));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private X509Certificate getCertificate(Ssl ssl, ApplicationContext context) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ssl.getKeyStoreType());
        keyStore.load(context.getResource(ssl.getKeyStore()).getInputStream(), ssl.getKeyStorePassword().toCharArray());
        return (X509Certificate)keyStore.getCertificate(ssl.getKeyAlias());

    }

    private long daysBetween(Date d1, Date d2) {
        return (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24);
    }

    private ConcurrentHashMap<String, String> generateMap(X509Certificate certificate) {
        return new ConcurrentHashMap<String, String>() {{
            put("subject", certificate.getSubjectDN().getName());
            put("issuer", certificate.getIssuerDN().getName());
//            put("dateNotAfter", certificate.getNotAfter().toString());
//            put("dateNotBefore", certificate.getNotBefore().toString());
            put("days", String.valueOf(daysBetween(certificate.getNotBefore(), certificate.getNotAfter())));
        }};
    }

    private void meterRegistration(MeterRegistry meterRegistry, ConcurrentHashMap<String, String> map) {
        map.entrySet().forEach(entry -> {
            meterRegistry.gaugeMapSize(name_metric,
                    List.of(Tag.of("key", entry.getKey()), Tag.of("value", entry.getValue())), map);
        });
    }

}
