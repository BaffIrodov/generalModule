package com.gen.GeneralModule.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PlayerForce {
    @Id
    @SequenceGenerator(name = "sq_player_force", sequenceName = "sq_player_force_id", allocationSize = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_player_force")
    public int id;

    public int playerId;
    public float playerForce;
    public int playerStability;
    public String map;
}
