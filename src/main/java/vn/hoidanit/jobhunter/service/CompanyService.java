package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Request.Meta;
import vn.hoidanit.jobhunter.domain.Request.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company create(Company param) throws IdInvalidException {
        Optional<Company> isCompany = this.companyRepository.findByName(param.getName());
        if (isCompany.isPresent()) {
            throw new IdInvalidException("Ten cong tyty da ton tai");
        }

        return this.companyRepository.save(param);
    }

    public ResultPaginationDTO getAll(Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageCompany.getNumber() + 1);// số trang
        meta.setPageSize(pageCompany.getSize());// số lượng phần tửtử

        meta.setPages(pageCompany.getTotalPages());// tổng số trang
        meta.setTotal(pageCompany.getTotalElements());// tổng số phần tử

        res.setMeta(meta);
        res.setResult(pageCompany.getContent());
        return res;
    }

    public ResultPaginationDTO getFiter(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);// số trang lấy từ param url
        meta.setPageSize(pageable.getPageSize());// số lượng phần tử lấy từ param url

        meta.setPages(pageCompany.getTotalPages());// tổng số trang
        meta.setTotal(pageCompany.getTotalElements());// tổng số phần tử

        res.setMeta(meta);
        res.setResult(pageCompany.getContent());
        return res;

    }

    public List<Company> getAll() {
        List<Company> pageCompany = this.companyRepository.findAll();

        return pageCompany;
    }

    public Company getByid(long id) throws IdInvalidException {
        Optional<Company> res = this.companyRepository.findById(id);
        if (!res.isPresent()) {
            throw new IdInvalidException("Id khong ton tai");
        }
        return res.get();

    }

    public Company updateByid(Company param) throws IdInvalidException {
        Company isCompany = this.getByid(param.getId());
        isCompany.setName(param.getName());
        isCompany.setDescription(param.getDescription());
        isCompany.setAddress(param.getAddress());
        isCompany.setLogo(param.getLogo());
        return this.companyRepository.save(isCompany);

    }

    public void removeByid(long id) throws IdInvalidException {
        this.getByid(id);
        this.companyRepository.deleteById(id);
    }

}
