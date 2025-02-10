package vn.pcv.jobhunter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.pcv.jobhunter.domain.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Long>,
JpaSpecificationExecutor<Skill> {
  Optional<Skill> findByName(String id);


  List<Skill> findByIdIn(List<Long> id);
}
