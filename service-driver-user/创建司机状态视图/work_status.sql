# 创建司机关系视图
create view `service-drive-user`.v_city_driver_user_work_status as

select t1.id as driver_id, t1.address as city_code, t2.work_status as work_status
from driver_user t1
         left join driver_user_work_status t2 on t1.id = t2.driver_id;

