package skyvangaurd.sms.authentication.web;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import skyvangaurd.sms.authentication.dto.LoginRequest;
import skyvangaurd.sms.authentication.service.UserService;
import skyvangaurd.sms.authentication.utils.JwtTokenBlacklistService;
import skyvangaurd.sms.authentication.utils.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class Controller {

  private final Logger logger = LoggerFactory.getLogger(getClass());
	private final UserService userService;

	@Autowired
	public Controller(UserService userService) {
		this.userService = userService;
	}
	
  @Autowired
	private JwtTokenBlacklistService jwtTokenBlacklistService;

  @Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

  @PostMapping(value = "/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest)
			throws Exception {
		try {
			SecurityContextHolder.getContext().setAuthentication(
					authenticationManager.authenticate(
							new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())));
		} catch (BadCredentialsException e) {
			return ResponseEntity.badRequest().body("Incorrect username or password");
		}

		String currentPrincipalName = SecurityContextHolder.getContext().getAuthentication().getName();
		final String jwt = jwtUtil.generateToken(currentPrincipalName);

		return ResponseEntity.ok(jwt);
	}

	@PostMapping(value = "/logout")
	public ResponseEntity<?> logoutUser(@RequestHeader(value = "Authorization") String token) {
		String tokenValue = token.substring(7); // Remove "Bearer " prefix
		long expiryDate = jwtUtil.extractExpiration(tokenValue).getTime();
		jwtTokenBlacklistService.blacklistToken(tokenValue, expiryDate);
		return ResponseEntity.ok().body("Successfully logged out");
	}

	@GetMapping(value = "/roles")
	public List<String> getAuthoritiesForUser(@RequestParam("email") String email) {
		return userService.getRolesForUser(email);
	}

	/**
	 * TODO end points:
	 *
	 * - /refresh: Refresh an authentication token.
	 * - /verify: Verify a token's validity.
	 *
	 */

  /**
	 * Maps UsernameNotFoundException to a 400 FORBIDDEN HTTP status code.
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ UsernameNotFoundException.class, IllegalArgumentException.class })
	public void handleBadRequest(Exception ex) {
		logger.error("Exception is: ", ex);
	}

	/**
	 * Maps AccessDeniedExceptions to a 403 FORBIDDEN HTTP status code.
	 */
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler({ AccessDeniedException.class, Exception.class })
	public void handleForbidden(Exception ex) {
		logger.error("Exception is: ", ex);
	}

	/**
	 * Maps NotFoundException to a 404 Not Found HTTP status code.
	 */
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler({NotFoundException.class, EntityNotFoundException.class})
	public void handleNotFound(Exception ex) {
		logger.error("Exception is: ", ex);
	}

	/**
	 * Maps DataIntegrityViolationException to a 409 Conflict HTTP status code.
	 */
	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler({ DataIntegrityViolationException.class })
	public void handleAlreadyExists(Exception ex) {
		logger.error("Exception is: ", ex);
	}

	/**
	 * Maps UnsupportedOperationException to a 501 Not Implemented HTTP status
	 * code.
	 */
	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	@ExceptionHandler({ UnsupportedOperationException.class })
	public void handleUnabletoReallocate(Exception ex) {
		logger.error("Exception is: ", ex);
	}
}
