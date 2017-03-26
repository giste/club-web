package org.giste.club.web.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.config.RestProperties;
import org.giste.club.web.service.exception.ClubNotFoundException;
import org.giste.club.web.service.exception.DuplicatedClubAcronymException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Implementation for {@link ClubService} interface.
 * 
 * @author Giste
 */
@Service
public class ClubServiceRest implements ClubService {

	private RestTemplate restTemplate;
	private RestProperties restProperties;
	
	public ClubServiceRest(RestTemplate restTemplate, RestProperties restProperties) {
		this.restTemplate = restTemplate;
		this.restProperties = restProperties;
	}

	@Override
	public List<ClubDto> findAll() {
		UriComponents uri = constructUriBuilder().path("/clubs").build();
		
		return Arrays.stream(restTemplate.getForObject(uri.toUriString(), ClubDto[].class))
				.collect(Collectors.toList());
	}

	@Override
	public ClubDto findById(long id) throws ClubNotFoundException {
		UriComponents uri = constructUriBuilder().path("/clubs/{id}").build();
		
		return restTemplate.getForObject(uri.toUriString(), ClubDto.class, id);
	}

	@Override
	public ClubDto create(ClubDto club) throws DuplicatedClubAcronymException {
		UriComponents uri = constructUriBuilder().path("/clubs").build();
		
		return restTemplate.postForObject(uri.toUriString(), club, ClubDto.class);
	}

	@Override
	public ClubDto update(ClubDto club) throws DuplicatedClubAcronymException {
		UriComponents uri = constructUriBuilder().path("/clubs/{id}").build();
		
		return restTemplate.exchange(uri.toUriString(), HttpMethod.PUT, new HttpEntity<>(club),
				ClubDto.class, club.getId()).getBody();
	}

	@Override
	public ClubDto disable(long id) {
		UriComponents uri = constructUriBuilder().path("/clubs/{id}/disable").build();
		
		return restTemplate.exchange(uri.toUriString(), HttpMethod.PUT, null,
				ClubDto.class, id).getBody();
	}

	@Override
	public ClubDto enable(long id) {
		UriComponents uri = constructUriBuilder().path("/clubs/{id}/enable").build();
		
		return restTemplate.exchange(uri.toUriString(), HttpMethod.PUT, null,
				ClubDto.class, id).getBody();
	}

	private UriComponentsBuilder constructUriBuilder() {
		return UriComponentsBuilder.newInstance()
				.scheme(restProperties.getScheme())
				.host(restProperties.getHost())
				.port(restProperties.getPort())
				.path(restProperties.getPath());
	}
}
