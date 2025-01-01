/*
 Navicat Premium Dump SQL

 Source Server         : 开发测试库-pg
 Source Server Type    : PostgreSQL
 Source Server Version : 160004 (160004)
 Source Host           : pgm-uf6713x8gfi411gdio.pg.rds.aliyuncs.com:5432
 Source Catalog        : jt
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 160004 (160004)
 File Encoding         : 65001

 Date: 01/01/2025 10:48:10
*/


-- ----------------------------
-- Table structure for data_dictionary
-- ----------------------------
DROP TABLE IF EXISTS "public"."data_dictionary";
CREATE TABLE "public"."data_dictionary" (
  "id" serial4 NOT NULL ,
  "key" varchar(100) COLLATE "pg_catalog"."default",
  "name" varchar(100) COLLATE "pg_catalog"."default",
  "value" varchar(100) COLLATE "pg_catalog"."default",
  "ext" json,
  "ext1" varchar(100) COLLATE "pg_catalog"."default",
  "langs" json,
  "sorted" int2 NOT NULL DEFAULT 100
)
;
COMMENT ON TABLE "public"."data_dictionary" IS '数据字典表';

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_menu";
CREATE TABLE "public"."t_menu" (
  "id" serial4 NOT NULL ,
  "parent_id" int4 NOT NULL DEFAULT 0,
  "region" varchar(30) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "enable" bool DEFAULT true,
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "code" varchar(255) COLLATE "pg_catalog"."default",
  "target" varchar(255) COLLATE "pg_catalog"."default",
  "path" varchar(255) COLLATE "pg_catalog"."default",
  "icon" varchar(255) COLLATE "pg_catalog"."default",
  "depth" int4,
  "ext" json,
  "langs" json,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "sort" int2 NOT NULL DEFAULT 100,
  "created_at" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."t_menu"."enable" IS '是否开启,';
COMMENT ON COLUMN "public"."t_menu"."langs" IS '多语言';
COMMENT ON COLUMN "public"."t_menu"."remark" IS '备注';
COMMENT ON TABLE "public"."t_menu" IS '菜单表';

-- ----------------------------
-- Table structure for t_permisson
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_permisson";
CREATE TABLE "public"."t_permisson" (
  "id" serial4 NOT NULL,
  "name" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "type" int2 NOT NULL,
  "code" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "target" varchar(255) COLLATE "pg_catalog"."default",
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "created_at" date,
  "updated_at" date
)
;
COMMENT ON COLUMN "public"."t_permisson"."type" IS '权限类型 1=权限标识,2=URL,3=页面元素ID';
COMMENT ON COLUMN "public"."t_permisson"."code" IS '权限编码';
COMMENT ON COLUMN "public"."t_permisson"."target" IS '权限保护的目标';
COMMENT ON COLUMN "public"."t_permisson"."description" IS '描述';
COMMENT ON TABLE "public"."t_permisson" IS '权限表';

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_role";
CREATE TABLE "public"."t_role" (
  "id" serial4 NOT NULL,
  "parent_id" int4 NOT NULL DEFAULT 0,
  "region" int4,
  "code" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "type" int2 NOT NULL DEFAULT 0,
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "is_show" bool NOT NULL DEFAULT true,
  "ext" json,
  "langs" json,
  "created_at" timestamp(0),
  "updated_at" timestamp(0)
)
;
COMMENT ON COLUMN "public"."t_role"."type" IS '角色类型 0=系统 1=自定义';
COMMENT ON TABLE "public"."t_role" IS '角色表';

-- ----------------------------
-- Table structure for t_role_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_role_menu";
CREATE TABLE "public"."t_role_menu" (
  "role_id" int4 NOT NULL,
  "menu_id" int4 NOT NULL
)
;
COMMENT ON TABLE "public"."t_role_menu" IS '角色菜单关联表';

-- ----------------------------
-- Table structure for t_role_permisson
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_role_permisson";
CREATE TABLE "public"."t_role_permisson" (
  "role_id" int4 NOT NULL,
  "permisson_id" int4 NOT NULL
)
;
COMMENT ON TABLE "public"."t_role_permisson" IS '角色权限表';

-- ----------------------------
-- Table structure for t_user_base
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_user_base";
CREATE TABLE "public"."t_user_base" (
  "id" serial8 NOT NULL,
  "org_id" int4 DEFAULT 0,
  "type" int4 DEFAULT 0,
  "user_name" varchar(20) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "mobile" varchar(20) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "email" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "password" varchar(60) COLLATE "pg_catalog"."default",
  "salt" varchar(29) COLLATE "pg_catalog"."default",
  "nick_name" varchar(30) COLLATE "pg_catalog"."default",
  "real_name" varchar(30) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "avatar" varchar(255) COLLATE "pg_catalog"."default",
  "status" int2 DEFAULT 1,
  "source" int4 DEFAULT 0,
  "gender" int4 DEFAULT 0,
  "verified" bool DEFAULT false,
  "firstlogin" bool DEFAULT true,
  "merge" bool DEFAULT false,
  "is_2fa" bool DEFAULT false,
  "secret_2fa" varchar(32) COLLATE "pg_catalog"."default",
  "operator" int8 DEFAULT 0,
  "deleted" bool NOT NULL DEFAULT false,
  "created_at" int4 NOT NULL,
  "updated_at" int4 NOT NULL
)
;
COMMENT ON COLUMN "public"."t_user_base"."org_id" IS '组织ID';
COMMENT ON COLUMN "public"."t_user_base"."type" IS '类型';
COMMENT ON COLUMN "public"."t_user_base"."user_name" IS '用户名';
COMMENT ON COLUMN "public"."t_user_base"."mobile" IS '手机号';
COMMENT ON COLUMN "public"."t_user_base"."password" IS '密码';
COMMENT ON COLUMN "public"."t_user_base"."salt" IS '盐值';
COMMENT ON COLUMN "public"."t_user_base"."nick_name" IS '昵称';
COMMENT ON COLUMN "public"."t_user_base"."real_name" IS '真实姓名';
COMMENT ON COLUMN "public"."t_user_base"."status" IS '用户状态 1=正常,0=禁用';
COMMENT ON COLUMN "public"."t_user_base"."source" IS '账户来源';
COMMENT ON COLUMN "public"."t_user_base"."gender" IS '性别';
COMMENT ON COLUMN "public"."t_user_base"."verified" IS '是否已实名认证';
COMMENT ON COLUMN "public"."t_user_base"."firstlogin" IS '是否首次登陆,0=不是，1=是';
COMMENT ON COLUMN "public"."t_user_base"."merge" IS '是否已经合并';
COMMENT ON COLUMN "public"."t_user_base"."is_2fa" IS '是否开启两部验证';
COMMENT ON COLUMN "public"."t_user_base"."secret_2fa" IS '两步验证密钥';
COMMENT ON COLUMN "public"."t_user_base"."operator" IS '操作人';
COMMENT ON TABLE "public"."t_user_base" IS '用户基础表';

-- ----------------------------
-- Table structure for t_user_oauth
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_user_oauth";
CREATE TABLE "public"."t_user_oauth" (
  "id" serial8 NOT NULL,
  "user_id" int8 NOT NULL,
  "type" int4 NOT NULL,
  "oauth_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "unionid" varchar(50) COLLATE "pg_catalog"."default",
  "credential" varchar(50) COLLATE "pg_catalog"."default",
  "created_at" timestamp(0)
)
;
COMMENT ON TABLE "public"."t_user_oauth" IS '用户开放平台信息表';

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_user_role";
CREATE TABLE "public"."t_user_role" (
  "user_id" int8 NOT NULL,
  "role_id" int4 NOT NULL
)
;
COMMENT ON TABLE "public"."t_user_role" IS '用户角色关联表';

-- ----------------------------
-- Primary Key structure for table data_dictionary
-- ----------------------------
ALTER TABLE "public"."data_dictionary" ADD CONSTRAINT "data_dictionary_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_menu
-- ----------------------------
ALTER TABLE "public"."t_menu" ADD CONSTRAINT "t_menu_pkey1" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_permisson
-- ----------------------------
ALTER TABLE "public"."t_permisson" ADD CONSTRAINT "t_permisson_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_role
-- ----------------------------
ALTER TABLE "public"."t_role" ADD CONSTRAINT "t_role_pkey1" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_role_menu
-- ----------------------------
ALTER TABLE "public"."t_role_menu" ADD CONSTRAINT "t_role_menu_pkey" PRIMARY KEY ("role_id", "menu_id");

-- ----------------------------
-- Primary Key structure for table t_role_permisson
-- ----------------------------
ALTER TABLE "public"."t_role_permisson" ADD CONSTRAINT "t_role_permisson_pkey" PRIMARY KEY ("role_id");

-- ----------------------------
-- Primary Key structure for table t_user_base
-- ----------------------------
ALTER TABLE "public"."t_user_base" ADD CONSTRAINT "t_user_base_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_user_oauth
-- ----------------------------
ALTER TABLE "public"."t_user_oauth" ADD CONSTRAINT "t_user_oauth_pkey1" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_user_role
-- ----------------------------
ALTER TABLE "public"."t_user_role" ADD CONSTRAINT "t_user_role_pkey" PRIMARY KEY ("user_id", "role_id");
