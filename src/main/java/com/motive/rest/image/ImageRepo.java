package com.motive.rest.image;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface ImageRepo extends CrudRepository<Image,UUID>{}
