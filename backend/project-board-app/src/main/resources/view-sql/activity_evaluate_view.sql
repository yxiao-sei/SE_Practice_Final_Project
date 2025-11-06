SELECT
	`activity_evaluate`.`id` AS `id`,
	`activity_evaluate`.`created_time` AS `created_time`,
	`activity_evaluate`.`creator` AS `creator`,
	`activity_evaluate`.`creator_name` AS `creator_name`,
	`activity_evaluate`.`is_removed` AS `is_removed`,
	`activity_evaluate`.`last_update_time` AS `last_update_time`,
	`activity_evaluate`.`last_updater` AS `last_updater`,
	`activity_evaluate`.`activity_evaluate` AS `activity_evaluate`,
	`activity_evaluate`.`activity_evaluate_level` AS `activity_evaluate_level`,
	`activity_evaluate`.`activity_id` AS `activity_id`,
	`activity_evaluate`.`member_department` AS `member_department`,
	`activity_evaluate`.`member_zy` AS `member_zy`,
	`activity_evaluate`.`state` AS `state`,
	`activity_evaluate`.`student_evaluate` AS `student_evaluate`,
	`activity_evaluate`.`student_evaluate_level` AS `student_evaluate_level`,
	`activity_evaluate`.`xh` AS `xh`,
	`activity_evaluate`.`file_id` AS `file_id`,
	`activity_evaluate`.`xm` AS `xm`,
	`activity_evaluate`.`summary` AS `summary`,
	`activity_member`.`is_achieve` AS `is_achieve`
FROM
	(
		`activity_evaluate`
		LEFT JOIN `activity_member` ON (((
					`activity_evaluate`.`activity_id` = `activity_member`.`activity_id`
					)
			AND ( `activity_member`.`is_removed` = FALSE )
	AND ( `activity_evaluate`.`xh` = `activity_member`.`member_xh` ))))