package com.qumu.kerberos.server.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.client.config.SunJaasKrb5LoginConfig;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${app.service-principal}")
	private String servicePrincipal;

	@Value("${app.keytab-location}")
	private String keytabLocation;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.exceptionHandling()
				.authenticationEntryPoint(spnegoEntryPoint())
				.and()
			.authorizeRequests()
				.antMatchers("/", "/status").permitAll()
				.anyRequest().authenticated()
				.and()
			.logout()
				.permitAll()
				.and()
			.addFilterBefore(
					spnegoAuthenticationProcessingFilter(authenticationManagerBean()),
					BasicAuthenticationFilter.class);
	}

	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {

		List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
		authenticationProviders.add(kerberosServiceAuthenticationProvider());

		ProviderManager authenticationManager = new ProviderManager(authenticationProviders);
		authenticationManager.setEraseCredentialsAfterAuthentication(false);

		return authenticationManager;
    }

	@Bean
	public SpnegoEntryPoint spnegoEntryPoint() {
		return new CustomSpnegoEntryPoint();
	}

	@Bean
	public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(
			AuthenticationManager authenticationManager) {
		SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
		filter.setAuthenticationManager(authenticationManager);
		return filter;
	}

	@Bean
	public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
		KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
		provider.setTicketValidator(sunJaasKerberosTicketValidator());
		provider.setUserDetailsService(new DummyUserDetailsService());
		return provider;
	}

	@Bean
	public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
		SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
		ticketValidator.setServicePrincipal(servicePrincipal);
		ticketValidator.setKeyTabLocation(new FileSystemResource(keytabLocation));
		ticketValidator.setDebug(true);
		return ticketValidator;
	}

	@Bean
	public SunJaasKrb5LoginConfig loginConfig() {
		SunJaasKrb5LoginConfig loginConfig = new SunJaasKrb5LoginConfig();
		loginConfig.setKeyTabLocation(new FileSystemResource(keytabLocation));
		loginConfig.setServicePrincipal(servicePrincipal);
		loginConfig.setDebug(true);
		loginConfig.setIsInitiator(true);
		return loginConfig;
	}
}
