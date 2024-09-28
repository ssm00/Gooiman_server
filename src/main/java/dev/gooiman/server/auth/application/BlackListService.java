package dev.gooiman.server.auth.application;

import dev.gooiman.server.auth.repository.BlackListRepository;
import dev.gooiman.server.auth.repository.entity.BlackList;
import dev.gooiman.server.common.dto.CommonSuccessDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlackListService {

    private final BlackListRepository blackListRepository;

    public boolean isExists(String id) {
        String bearerToken = "Bearer " + id;
        Optional<BlackList> token = blackListRepository.findById(bearerToken);
        return token.isPresent();
    }

    public CommonSuccessDto saveBlackList(String token) {
        BlackList entity = new BlackList(token);
        blackListRepository.save(entity);
        return CommonSuccessDto.fromEntity(true);
    }
}
