package ru.skypro.hogwarts.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.hogwarts.entities.Avatar;
import ru.skypro.hogwarts.repositories.AvatarRepository;

import java.util.List;

@RestController
@RequestMapping("/avatars")
public class AvatarController {

    private final AvatarRepository avatarRepository;

    public AvatarController(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @GetMapping
    public List<Avatar> getAvatars(@RequestParam(defaultValue = "1") Integer pageNumber,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}
