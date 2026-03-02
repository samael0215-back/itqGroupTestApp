package itqGroupTestApp.core.servises;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import itqGroupTestApp.core.entity.User;
import itqGroupTestApp.core.ropositories.UserRepository;
import itqGroupTestApp.core.utilityClasses.ServiceException;

import java.io.Serializable;

@Slf4j
@Service
public class UserService implements Serializable {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long authorId) {
        return userRepository.findById(authorId).orElseThrow(() -> {
            log.error("User not found for id: {}", authorId);
            return new ServiceException("Пользователь не найден с id: " + authorId);
        });
    }
}
