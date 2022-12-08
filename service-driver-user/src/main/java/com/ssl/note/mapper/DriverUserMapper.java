package com.ssl.note.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ssl.note.dto.DriverUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverUserMapper extends BaseMapper<DriverUser> {

    int selectDriverUserCountByCityCode(@Param("cityCode") String cityCode);
}
