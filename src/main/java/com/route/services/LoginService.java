package com.route.services;

import com.route.models.LoginRequest;
import com.route.models.Users;
import com.route.models.Tentative;
import com.route.repositories.UserRepository;
import com.route.repositories.TentativeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginService {
	private final UserRepository userRepository;
	private final TentativeRepository tentativeRepository;

	private static final int MAX_FAILED_ATTEMPTS = 3;

	public LoginService(UserRepository userRepository, TentativeRepository tentativeRepository) {
		this.userRepository = userRepository;
		this.tentativeRepository = tentativeRepository;
	}

	/**
	 * Authenticate a user by identifiant and password.
	 * Updates dateDerniereConnexion on successful login.
	 * Records a Tentative for every attempt.
	 * Blocks user after MAX_FAILED_ATTEMPTS failed attempts.
	 * Throws RuntimeException when credentials are invalid or account blocked.
	 */
	public Users login(LoginRequest loginRequest) {
		// find user by identifiant (may be null)
		Users maybeUser = userRepository.findByIdentifiant(loginRequest.getIdentifiant());

		// If user exists and is blocked, record failed attempt and throw
		if (maybeUser != null && Boolean.TRUE.equals(maybeUser.getBlocked())) {
			Tentative tentativeBlocked = new Tentative();
			tentativeBlocked.setDateTentative(LocalDateTime.now());
			tentativeBlocked.setSucces(false);
			tentativeBlocked.setUser(maybeUser);
			tentativeRepository.save(tentativeBlocked);
			throw new RuntimeException("Compte bloqué");
		}

		// check credentials
		Users user = userRepository.findByIdentifiantAndPassword(loginRequest.getIdentifiant(), loginRequest.getPassword());

		// create and save attempt record
		Tentative tentative = new Tentative();
		tentative.setDateTentative(LocalDateTime.now());
		tentative.setSucces(user != null);
			// attach the known user entity when possible
		tentative.setUser(maybeUser); // may be null
		tentativeRepository.save(tentative);

		if (user != null) {
			// successful login: reset failed attempts and ensure not blocked
			user.setDateDerniereConnexion(LocalDateTime.now());
			user.setFailedAttempts(0);
			user.setBlocked(false);
			userRepository.save(user);
			return user;
		} else {
			// failed login: increment failed attempts if user exists
			if (maybeUser != null) {
				int attempts = maybeUser.getFailedAttempts() == null ? 0 : maybeUser.getFailedAttempts();
				attempts++;
				maybeUser.setFailedAttempts(attempts);
				if (attempts >= MAX_FAILED_ATTEMPTS) {
					maybeUser.setBlocked(true);
				}
				userRepository.save(maybeUser);
			}
			throw new RuntimeException("Identifiants incorrects");
		}
	}

	/**
	 * Unblock the user identified by identifiant: set blocked=false and reset failedAttempts.
	 * Throws RuntimeException if user not found.
	 */
	public Users unblockUser(String identifiant) {
		Users user = userRepository.findByIdentifiant(identifiant);
		if (user == null) {
			throw new RuntimeException("Utilisateur non trouvé");
		}
		user.setBlocked(false);
		user.setFailedAttempts(0);
		return userRepository.save(user);
	}

	/**
	 * Register a new user. Throws IllegalArgumentException if identifiant already exists.
	 */
	public Users register(Users newUser) {
		if (userRepository.findByIdentifiant(newUser.getIdentifiant()) != null) {
			throw new IllegalArgumentException("IdentifiantExists: L'identifiant existe déjà, veuillez en choisir un autre.");
		}
		if (newUser.getDateCreation() == null) {
			newUser.setDateCreation(LocalDateTime.now());
		}
		return userRepository.save(newUser);
	}
}