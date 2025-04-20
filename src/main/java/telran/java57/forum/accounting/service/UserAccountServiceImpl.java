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
    final UserAccountRepository userAccountRepository;
    final ModelMapper modelMapper;

    @Override
    public UserDto register(UserRegisterDto userRegisterDto) {
        if ( userAccountRepository.existsById(userRegisterDto.getLogin()) ) {
            throw new UserExistsException();
        }
        UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
        String password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
        userAccount.setPassword(password);
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto getUser(String login) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException :: new);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto removeUser(String login) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException :: new);
        userAccountRepository.delete(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto updateUser(String login, UpdateUserDto updateUserDto) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException :: new);
        if ( updateUserDto.getFirstName() != null ) {
            userAccount.setFirstName(updateUserDto.getFirstName());
        }
        if ( updateUserDto.getLastName() != null ) {
            userAccount.setLastName(updateUserDto.getLastName());
        }
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException :: new);
        boolean res;
        if ( isAddRole ) {
            res = userAccount.addRole(role);
        } else {
            res = userAccount.removeRole(role);
        }
        if ( res ) {
            userAccountRepository.save(userAccount);
        }
        return modelMapper.map(userAccount, RolesDto.class);
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword) {
        UserAccount userAccount = userAccountRepository.findById(login)
                .orElseThrow(UserNotFoundException :: new);
        if ( ! BCrypt.checkpw(oldPassword, userAccount.getPassword()) ) {
            throw new InvalidPasswordException();
        }
        userAccount.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userAccountRepository.save(userAccount);
    }

    @Override
    public void run(String... args) throws Exception {
        if ( ! userAccountRepository.existsById("admin") ) {

            String password = BCrypt.hashpw("admin", BCrypt.gensalt());
            UserAccount userAccount = new UserAccount("admin", password, "", "");
            userAccount.addRole("MODERATOR");
            userAccount.addRole("ADMINISTRATOR");
            userAccountRepository.save(userAccount);
        }
    }

}
