package telran.java57.forum.accounting.service;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import telran.java57.forum.accounting.dao.UserAccountRepository;
import telran.java57.forum.accounting.dto.RolesDto;
import telran.java57.forum.accounting.dto.UpdateUserDto;
import telran.java57.forum.accounting.dto.UserDto;
import telran.java57.forum.accounting.dto.UserRegisterDto;
import telran.java57.forum.accounting.dto.exceptions.InvalidPasswordException;
import telran.java57.forum.accounting.dto.exceptions.UserExistsException;
import telran.java57.forum.accounting.dto.exceptions.UserNotFoundException;
import telran.java57.forum.accounting.model.UserAccount;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService, CommandLineRunner {
    final UserAccountRepository userAccountRepository; //хранилище данных пользователей
    final ModelMapper modelMapper; //объект-инструмент для преобразования данных(объектов) из одного формата в другой

    @Override
    public UserDto register(UserRegisterDto userRegisterDto) {
        //проверяем существует ли пользователь с таким логином
        if (userAccountRepository.existsById(userRegisterDto.getLogin())) {
            throw new UserExistsException();
        }
        //преобразуем DTO в сущность модели(создаем нового пользователя)
        UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
        //хешируем пароль с помощью BCrypt (алгоритм шифрования)
        String password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
        userAccount.setPassword(password);
        //сохраняем пользователя в хранилище данных и возвращаем его DTO
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto getUser(String login) {
        //находим пользователя по логину в хранилище данных
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto removeUser(String login) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
        userAccountRepository.delete(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto updateUser(String login, UpdateUserDto updateUserDto) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
        //обновляем только те поля, кот.пришли в запросе(если в DTO есть имя и фамилия, то обновляем их)
        if (updateUserDto.getFirstName() != null) {
            userAccount.setFirstName(updateUserDto.getFirstName());
        }
        if (updateUserDto.getLastName() != null) {
            userAccount.setLastName(updateUserDto.getLastName());
        }
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
        boolean res;
        //добавляем или удаляем роль в списке ролей пользователя в зависимости от флага isAddRole
        if (isAddRole) {
            res = userAccount.addRole(role);
        } else {
            res = userAccount.removeRole(role);
        }
        //сохраняем только если были изменения (в ролях)
        if (res) {
            userAccountRepository.save(userAccount);
        }
        return modelMapper.map(userAccount, RolesDto.class);
    }
//    @Override
//    public void changePassword(String login, String newPassword) {
//        // Находим пользователя по логину или выбрасываем исключение
//        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
//        // Хешируем новый пароль с помощью BCrypt
//        String password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
//        // Устанавливаем захешированный пароль
//        userAccount.setPassword(password);
//        // Сохраняем пользователя с обновленным паролем
//        userAccountRepository.save(userAccount);
//    }

//    @Override
//    public void changePassword(String login, String oldPassword, String newPassword) {
//        UserAccount userAccount = userAccountRepository.findById(login)
//                .orElseThrow(() -> new UserNotFoundException());
//
//        if (!BCrypt.checkpw(oldPassword, userAccount.getPassword())) {
//            throw new BadCredentialsException("Wrong password");
//        }
//
//        userAccount.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
//        userAccountRepository.save(userAccount);
//    }
@Override
public void changePassword(String login, String oldPassword, String newPassword) {
    UserAccount userAccount = userAccountRepository.findById(login)
            .orElseThrow(UserNotFoundException::new);

    if (!BCrypt.checkpw(oldPassword, userAccount.getPassword())) {
        throw new InvalidPasswordException();
    }

    userAccount.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
    userAccountRepository.save(userAccount);
}

    // Для метода run нужно реализовать интерфейс CommandLineRunner
    @Override
    public void run(String... args) throws Exception {
        // Проверяем существует ли администратор при старте приложения
        if (!userAccountRepository.existsById("admin")) {
            // Хешируем пароль администратора с помощью BCrypt
            String password = BCrypt.hashpw("admin", BCrypt.gensalt());
            // Создаем учетную запись администратора
            UserAccount userAccount = new UserAccount("admin", password, "", "");
            // Добавляем роли модератора и администратора
            userAccount.addRole("MODERATOR");
            userAccount.addRole("ADMINISTRATOR");
            // Сохраняем администратора в базу данных
            userAccountRepository.save(userAccount);
        }
    }
//    Запуск приложения
//      │
//      ▼
//Загрузка всех бинов
//      │
//      ▼
//Вызов методов run() у всех CommandLineRunner
//      │
//      ▼
//Наш метод run() проверяет наличие администратора
//      │
//      ▼
//Если нет - создаем администратора с правами MODERATOR и ADMINISTRATOR


  //     => Без этого механизма при первом запуске не было бы пользователя с правами администратора,
   //      и мы не смогли бы выполнять административные задачи в приложении

}
