package com.appdeveloper.photoapp.api.users.service;

import com.appdeveloper.photoapp.api.users.data.AlbumsServiceClient;
import com.appdeveloper.photoapp.api.users.data.UserEntity;
import com.appdeveloper.photoapp.api.users.data.UsersRepository;
import com.appdeveloper.photoapp.api.users.shared.UserDto;
import com.appdeveloper.photoapp.api.users.ui.model.AlbumResponseModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class UsersServiceImpl implements UsersService{

    UsersRepository usersRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
  //  RestTemplate restTemplate;
    Environment environment;
    AlbumsServiceClient albumsServiceClient;
    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository,AlbumsServiceClient albumsServiceClient,Environment environment,BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.usersRepository=usersRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.albumsServiceClient=albumsServiceClient;
        this.environment=environment;
    }
    @Override
    public UserDto createUser(UserDto userDetails) {
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        ModelMapper modelMapper=new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity =modelMapper.map(userDetails, UserEntity.class);
      //  userEntity.setEncryptedPassword("test");
        usersRepository.save(userEntity);
        UserDto returnValue=modelMapper.map(userEntity,UserDto.class);
        return returnValue;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity=usersRepository.findByEmail(email);
        if(userEntity==null) throw new UsernameNotFoundException(email);

        return new ModelMapper().map(userEntity,UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity=usersRepository.findByUserId(userId);
        if(userEntity==null) throw new UsernameNotFoundException("user not found");
        UserDto userDto=new ModelMapper().map(userEntity,UserDto.class);


     //    String albumsUrl = String.format(environment.getProperty("albums.url"), userId);
     //   String albumsUrl=String.format("http://USERS-WS/users/%s/albums",userId);

     /*   ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
        });
        List<AlbumResponseModel> albumsList = albumsListResponse.getBody(); */
        List<AlbumResponseModel>albumsList= albumsServiceClient.getAlbums(userId);


        userDto.setAlbums(albumsList);
        return userDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       UserEntity userEntity= usersRepository.findByEmail(username);
       if(userEntity==null) throw  new UsernameNotFoundException(username);
        return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),
                true,true,true, true,new ArrayList<>());
    }
}
