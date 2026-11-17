package service;

import dto.request.AuthRequest;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class AuthService {
    @Inject
    UserRepository userRepository;

    public boolean register(AuthRequest request) {
        if (userRepository.findByLogin(request.getLogin()) != null) {
            return false;
        }
        User user = new User();
        user.setLogin(request.getLogin());
        user.setHashPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);
        return true;
    }

    public boolean authenticate(AuthRequest request) {
        User user = userRepository.findByLogin(request.getLogin());
        if (user == null) {
            return false;
        }
        return BCrypt.checkpw(request.getPassword(), user.getHashPassword());
    }
}
