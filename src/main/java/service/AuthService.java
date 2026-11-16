package service;

import dto.request.AuthRequest;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/** Регистрация и проверка пароля. Пароли хранятся только в виде BCrypt-хэша. */
@ApplicationScoped
@Transactional
public class AuthService {
    @Inject
    UserRepository userRepository;

    /** @return false, если логин уже занят */
    public boolean register(AuthRequest request) {
        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            return false;
        }
        User user = new User();
        user.setLogin(request.getLogin());
        user.setHashPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);
        return true;
    }

    /** @return true, если пользователь существует и пароль верный */
    public boolean authenticate(AuthRequest request) {
        return userRepository.findByLogin(request.getLogin())
                .map(user -> BCrypt.checkpw(request.getPassword(), user.getHashPassword()))
                .orElse(false);
    }
}
