package org.giste.club.web.config;

import org.giste.spring.util.config.RestPropertiesImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class for reading properties to communicate with REST server.
 * 
 * @author Giste
 */
@Component
@ConfigurationProperties(prefix = "rest")
public class ClubRestProperties extends RestPropertiesImpl {

}
