package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

}