package com.awd.gateway;

import com.awd.lbconfig.CandidatRandomLoadBalancerConfig;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Partie 3 - Question 3 : appliquer l'algorithme Random UNIQUEMENT au service "candidat".
 *
 * Le nom doit être exactement celui utilisé dans l'URI de la route (lb://candidat).
 * Les autres services (job, meeting, ...) gardent l'algorithme par défaut (Round Robin).
 *
 * Active seulement avec le profil "random-lb", pour pouvoir comparer
 * Round Robin (sans le profil) et Random (avec le profil).
 */
@Configuration
@Profile("random-lb")
@LoadBalancerClient(name = "candidat", configuration = CandidatRandomLoadBalancerConfig.class)
public class RandomLoadBalancerActivation {
}
