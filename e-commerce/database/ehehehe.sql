USE [test_ecommerce]
GO
/****** Object:  Table [dbo].[addresses]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[addresses](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[street] [varchar](255) NOT NULL,
	[city] [varchar](100) NULL,
	[state] [varchar](100) NULL,
	[country] [varchar](100) NULL,
	[postal_code] [varchar](20) NOT NULL,
	[is_default] [bit] NOT NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
	[street_address] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[categories]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[categories](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NOT NULL,
	[description] [varchar](255) NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[features]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[features](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[created_at] [datetime2](6) NOT NULL,
	[description] [varchar](255) NOT NULL,
	[feature_key] [varchar](255) NOT NULL,
	[name] [varchar](255) NOT NULL,
	[updated_at] [datetime2](6) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[order_code]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order_code](
	[salecode_id] [int] NOT NULL,
	[orders_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[salecode_id] ASC,
	[orders_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[order_items]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order_items](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[order_id] [bigint] NOT NULL,
	[product_id] [bigint] NOT NULL,
	[quantity] [int] NOT NULL,
	[price_at_time] [decimal](10, 2) NOT NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
	[price_at_purchase] [numeric](10, 2) NOT NULL,
	[seller_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[order_salecode]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order_salecode](
	[order_id] [bigint] NOT NULL,
	[salecode_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[order_id] ASC,
	[salecode_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[orders]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[orders](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[customer_id] [bigint] NOT NULL,
	[shipping_address_id] [bigint] NOT NULL,
	[total_amount] [decimal](10, 2) NOT NULL,
	[status] [varchar](50) NOT NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
	[order_status] [varchar](20) NOT NULL,
	[shipping_cost] [numeric](10, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_images]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_images](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[product_id] [bigint] NOT NULL,
	[image_url] [varchar](512) NULL,
	[is_primary] [bit] NOT NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[products]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[products](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NOT NULL,
	[description] [text] NULL,
	[price] [decimal](10, 2) NOT NULL,
	[stock_quantity] [int] NOT NULL,
	[seller_id] [bigint] NOT NULL,
	[category_id] [bigint] NOT NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
	[sku] [varchar](255) NULL,
	[status] [varchar](20) NOT NULL,
	[version] [bigint] NULL,
	[image_url] [varchar](255) NULL,
	[primary_image_url] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[role_features]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[role_features](
	[role_id] [bigint] NOT NULL,
	[feature_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[role_id] ASC,
	[feature_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[roles]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[roles](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NOT NULL,
	[description] [varchar](255) NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[salecode]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[salecode](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NOT NULL,
	[discount_percent] [decimal](5, 2) NOT NULL,
	[start_date] [datetime2](7) NOT NULL,
	[end_date] [datetime2](7) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
	[quantity] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[user_roles]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[user_roles](
	[user_id] [bigint] NOT NULL,
	[role_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[user_id] ASC,
	[role_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[users]    Script Date: 5/6/2025 10:20:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[users](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[email] [varchar](255) NOT NULL,
	[password] [varchar](255) NOT NULL,
	[first_name] [varchar](255) NULL,
	[last_name] [varchar](255) NULL,
	[shop_name] [varchar](255) NULL,
	[is_active] [bit] NOT NULL,
	[is_verified] [bit] NOT NULL,
	[verification_token] [varchar](255) NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
	[img] [varchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[categories] ON 

INSERT [dbo].[categories] ([id], [name], [description], [created_at], [updated_at]) VALUES (2, N'Clothing', N'quanao', CAST(N'2025-05-05T00:00:00.0000000' AS DateTime2), CAST(N'2025-05-05T00:00:00.0000000' AS DateTime2))
INSERT [dbo].[categories] ([id], [name], [description], [created_at], [updated_at]) VALUES (3, N'Accessories', N'mypham', CAST(N'2025-05-05T00:00:00.0000000' AS DateTime2), CAST(N'2025-05-05T00:00:00.0000000' AS DateTime2))
INSERT [dbo].[categories] ([id], [name], [description], [created_at], [updated_at]) VALUES (5, N'Laptop', N'laptop hehe', CAST(N'2025-05-05T00:00:00.0000000' AS DateTime2), CAST(N'2025-05-05T00:00:00.0000000' AS DateTime2))
INSERT [dbo].[categories] ([id], [name], [description], [created_at], [updated_at]) VALUES (6, N'Apple', N'tao can do', CAST(N'2025-05-05T00:00:00.0000000' AS DateTime2), CAST(N'2025-05-05T00:00:00.0000000' AS DateTime2))
SET IDENTITY_INSERT [dbo].[categories] OFF
GO
SET IDENTITY_INSERT [dbo].[products] ON 

INSERT [dbo].[products] ([id], [name], [description], [price], [stock_quantity], [seller_id], [category_id], [created_at], [updated_at], [sku], [status], [version], [image_url], [primary_image_url]) VALUES (1, N'meo`omdau`', N'12313', CAST(155000.00 AS Decimal(10, 2)), 1, 8, 2, CAST(N'2025-05-05T06:21:37.6427645' AS DateTime2), CAST(N'2025-05-05T10:16:16.1405230' AS DateTime2), N'3', N'ACTIVE', NULL, N'/uploads/330cb9e6-2117-4c78-9697-51a6bfee70ea.png', N'/uploads/330cb9e6-2117-4c78-9697-51a6bfee70ea.png')
INSERT [dbo].[products] ([id], [name], [description], [price], [stock_quantity], [seller_id], [category_id], [created_at], [updated_at], [sku], [status], [version], [image_url], [primary_image_url]) VALUES (3, N'meo`heeeheh', N'tao la bo chung may, chung may la con tao, hehehehhehehehehehehehehe', CAST(145000.00 AS Decimal(10, 2)), 1, 8, 3, CAST(N'2025-05-05T06:22:45.1822499' AS DateTime2), CAST(N'2025-05-05T10:06:50.6112869' AS DateTime2), N'122', N'ACTIVE', NULL, N'/uploads/2ee0a70a-c5ae-4ac8-9538-e2531c5ba5db.jpg', N'/uploads/2ee0a70a-c5ae-4ac8-9538-e2531c5ba5db.jpg')
INSERT [dbo].[products] ([id], [name], [description], [price], [stock_quantity], [seller_id], [category_id], [created_at], [updated_at], [sku], [status], [version], [image_url], [primary_image_url]) VALUES (4, N'Con ga con', N'Giandaozidededed', CAST(3234000.00 AS Decimal(10, 2)), 1, 8, 3, CAST(N'2025-05-05T07:00:50.0159210' AS DateTime2), CAST(N'2025-05-05T07:01:31.9975160' AS DateTime2), N'122', N'ACTIVE', NULL, N'/uploads/f95b7628-9b04-4d89-9dbe-e20a7386ebd1.jpg', N'/uploads/f95b7628-9b04-4d89-9dbe-e20a7386ebd1.jpg')
INSERT [dbo].[products] ([id], [name], [description], [price], [stock_quantity], [seller_id], [category_id], [created_at], [updated_at], [sku], [status], [version], [image_url], [primary_image_url]) VALUES (11, N'Iphone16 promax hehehee', N'312', CAST(1000000.00 AS Decimal(10, 2)), 1, 8, 6, CAST(N'2025-05-06T07:01:27.0627043' AS DateTime2), CAST(N'2025-05-06T07:01:43.6836695' AS DateTime2), N'100', N'ACTIVE', NULL, N'/uploads/6bda2b97-8d6d-4d72-bb97-d02a94cda0e2.jpg', N'/uploads/6bda2b97-8d6d-4d72-bb97-d02a94cda0e2.jpg')
INSERT [dbo].[products] ([id], [name], [description], [price], [stock_quantity], [seller_id], [category_id], [created_at], [updated_at], [sku], [status], [version], [image_url], [primary_image_url]) VALUES (13, N'Con ga con123', N'1231fdsfsd

[ADMIN REJECTION NOTE: chua tai dau em ]', CAST(150000.00 AS Decimal(10, 2)), 0, 8, 5, CAST(N'2025-05-06T08:49:37.4990293' AS DateTime2), CAST(N'2025-05-06T09:46:45.4558402' AS DateTime2), N'100', N'REJECTED', NULL, NULL, N'/uploads/b5ed66c8-f9fb-4403-8625-daab0cf099fc.png')
INSERT [dbo].[products] ([id], [name], [description], [price], [stock_quantity], [seller_id], [category_id], [created_at], [updated_at], [sku], [status], [version], [image_url], [primary_image_url]) VALUES (14, N'Con ga con123', N'ddfdffd', CAST(150000.00 AS Decimal(10, 2)), 2, 8, 3, CAST(N'2025-05-06T09:40:53.6861019' AS DateTime2), CAST(N'2025-05-06T09:41:09.7638260' AS DateTime2), N'100', N'ACTIVE', NULL, NULL, N'/uploads/8ed8f7e2-cb46-4898-b68c-e92c4c250821.jpg')
INSERT [dbo].[products] ([id], [name], [description], [price], [stock_quantity], [seller_id], [category_id], [created_at], [updated_at], [sku], [status], [version], [image_url], [primary_image_url]) VALUES (15, N'LAPTOP hehe', N'doisnfodsnsdvsovsuouovfuoou', CAST(200000.00 AS Decimal(10, 2)), 10, 10, 5, CAST(N'2025-05-06T10:14:10.4299716' AS DateTime2), CAST(N'2025-05-06T10:14:10.4299716' AS DateTime2), N'100', N'PENDING_APPROVAL', NULL, NULL, N'/uploads/4ec8bb37-3a2c-4f95-a465-f6a19c19302b.jpg')
INSERT [dbo].[products] ([id], [name], [description], [price], [stock_quantity], [seller_id], [category_id], [created_at], [updated_at], [sku], [status], [version], [image_url], [primary_image_url]) VALUES (16, N'IPHONE samsung', N'tao la samsung bo cua apple', CAST(15000000.00 AS Decimal(10, 2)), 200, 10, 6, CAST(N'2025-05-06T10:14:55.1116740' AS DateTime2), CAST(N'2025-05-06T10:14:55.1116740' AS DateTime2), N'200', N'PENDING_APPROVAL', NULL, NULL, N'/uploads/19441a56-c366-4130-8885-58a6aa05d091.jpg')
SET IDENTITY_INSERT [dbo].[products] OFF
GO
SET IDENTITY_INSERT [dbo].[roles] ON 

INSERT [dbo].[roles] ([id], [name], [description], [created_at], [updated_at]) VALUES (1, N'ADMIN', N'Administrator role with full access', CAST(N'2025-04-25T13:22:12.5500000' AS DateTime2), CAST(N'2025-04-25T13:22:12.5500000' AS DateTime2))
INSERT [dbo].[roles] ([id], [name], [description], [created_at], [updated_at]) VALUES (2, N'CUSTOMER', N'Regular customer role', CAST(N'2025-04-25T13:22:12.5500000' AS DateTime2), CAST(N'2025-04-25T13:22:12.5500000' AS DateTime2))
INSERT [dbo].[roles] ([id], [name], [description], [created_at], [updated_at]) VALUES (3, N'SELLER', N'Merchant role for selling products', CAST(N'2025-04-25T13:22:12.5500000' AS DateTime2), CAST(N'2025-04-25T13:22:12.5500000' AS DateTime2))
SET IDENTITY_INSERT [dbo].[roles] OFF
GO
INSERT [dbo].[user_roles] ([user_id], [role_id]) VALUES (8, 3)
INSERT [dbo].[user_roles] ([user_id], [role_id]) VALUES (9, 2)
INSERT [dbo].[user_roles] ([user_id], [role_id]) VALUES (10, 3)
INSERT [dbo].[user_roles] ([user_id], [role_id]) VALUES (11, 1)
GO
SET IDENTITY_INSERT [dbo].[users] ON 

INSERT [dbo].[users] ([id], [email], [password], [first_name], [last_name], [shop_name], [is_active], [is_verified], [verification_token], [created_at], [updated_at], [img]) VALUES (8, N'vtrgiangg2903@gmail.com', N'$2a$10$6Nx.wptMp/aw1WsM.q/cpOic7w.TU2ryW45ePf/uYg7oXRWj1Kosm', N'Vu', N'Giang', N'POKEMONghehe', 1, 1, NULL, CAST(N'2025-04-25T14:06:01.6993181' AS DateTime2), CAST(N'2025-05-06T09:00:34.1960783' AS DateTime2), NULL)
INSERT [dbo].[users] ([id], [email], [password], [first_name], [last_name], [shop_name], [is_active], [is_verified], [verification_token], [created_at], [updated_at], [img]) VALUES (9, N'giangcr7dz@gmail.com', N'$2a$10$6ODYoNszslPIjOw5qFzaduPTsCu49g/0rhbjTMSFGRkRoJv/hQhEa', N'Vu', N'Giang', NULL, 1, 0, N'a95031f3-cc33-4a3a-aa72-fc4d1b97bd24', CAST(N'2025-05-04T22:06:04.1861362' AS DateTime2), CAST(N'2025-05-04T22:06:04.1861362' AS DateTime2), NULL)
INSERT [dbo].[users] ([id], [email], [password], [first_name], [last_name], [shop_name], [is_active], [is_verified], [verification_token], [created_at], [updated_at], [img]) VALUES (10, N'giangvthe187264@fpt.edu.vn', N'$2a$10$gy7OZq2FKnqmAs8uPdrROOeBK9iRu.VQt5VwgjPu7PEh9o5Y02I72', N'Vu', N'Giang', N'LongRiver', 1, 0, N'd577f567-5a8b-4e51-80e6-c4501dcf1698', CAST(N'2025-05-04T22:29:56.3451672' AS DateTime2), CAST(N'2025-05-04T22:29:56.3451672' AS DateTime2), NULL)
INSERT [dbo].[users] ([id], [email], [password], [first_name], [last_name], [shop_name], [is_active], [is_verified], [verification_token], [created_at], [updated_at], [img]) VALUES (11, N'taotet190@gmail.com', N'$2a$10$x.hk6EXANbtuJv/Vp/nwCe6Qaq4Y4.QDTmGzHHpP375YbCj6W5O4m', N'ga', N'ga', NULL, 1, 0, N'51663273-dea7-44ec-a4df-de7a748d7ec9', CAST(N'2025-05-06T07:12:15.6886762' AS DateTime2), CAST(N'2025-05-06T10:05:59.2777551' AS DateTime2), NULL)
SET IDENTITY_INSERT [dbo].[users] OFF
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__categori__72E12F1BD17D35AF]    Script Date: 5/6/2025 10:20:57 AM ******/
ALTER TABLE [dbo].[categories] ADD UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UK_b6qhwd2ah4bi6b4dlaflberpm]    Script Date: 5/6/2025 10:20:57 AM ******/
ALTER TABLE [dbo].[features] ADD  CONSTRAINT [UK_b6qhwd2ah4bi6b4dlaflberpm] UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UK_evu1o2aikw59pxvmtejurmo57]    Script Date: 5/6/2025 10:20:57 AM ******/
ALTER TABLE [dbo].[features] ADD  CONSTRAINT [UK_evu1o2aikw59pxvmtejurmo57] UNIQUE NONCLUSTERED 
(
	[feature_key] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__roles__72E12F1BB60A9B6A]    Script Date: 5/6/2025 10:20:57 AM ******/
ALTER TABLE [dbo].[roles] ADD UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__salecode__357D4CF90CCDEB4C]    Script Date: 5/6/2025 10:20:57 AM ******/
ALTER TABLE [dbo].[salecode] ADD UNIQUE NONCLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__users__AB6E616434A48221]    Script Date: 5/6/2025 10:20:57 AM ******/
ALTER TABLE [dbo].[users] ADD UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[addresses] ADD  DEFAULT ((0)) FOR [is_default]
GO
ALTER TABLE [dbo].[product_images] ADD  DEFAULT ((0)) FOR [is_primary]
GO
ALTER TABLE [dbo].[users] ADD  DEFAULT ((1)) FOR [is_active]
GO
ALTER TABLE [dbo].[users] ADD  DEFAULT ((0)) FOR [is_verified]
GO
ALTER TABLE [dbo].[addresses]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[order_code]  WITH CHECK ADD  CONSTRAINT [FKihot2b244sycgk6j5o4ffd5af] FOREIGN KEY([orders_id])
REFERENCES [dbo].[orders] ([id])
GO
ALTER TABLE [dbo].[order_code] CHECK CONSTRAINT [FKihot2b244sycgk6j5o4ffd5af]
GO
ALTER TABLE [dbo].[order_items]  WITH CHECK ADD FOREIGN KEY([order_id])
REFERENCES [dbo].[orders] ([id])
GO
ALTER TABLE [dbo].[order_items]  WITH CHECK ADD FOREIGN KEY([product_id])
REFERENCES [dbo].[products] ([id])
GO
ALTER TABLE [dbo].[order_items]  WITH CHECK ADD  CONSTRAINT [FKiteu7744jhts0njdk0g9cmew6] FOREIGN KEY([seller_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[order_items] CHECK CONSTRAINT [FKiteu7744jhts0njdk0g9cmew6]
GO
ALTER TABLE [dbo].[order_salecode]  WITH CHECK ADD FOREIGN KEY([order_id])
REFERENCES [dbo].[orders] ([id])
GO
ALTER TABLE [dbo].[order_salecode]  WITH CHECK ADD FOREIGN KEY([salecode_id])
REFERENCES [dbo].[salecode] ([id])
GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD FOREIGN KEY([customer_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD FOREIGN KEY([shipping_address_id])
REFERENCES [dbo].[addresses] ([id])
GO
ALTER TABLE [dbo].[product_images]  WITH CHECK ADD FOREIGN KEY([product_id])
REFERENCES [dbo].[products] ([id])
GO
ALTER TABLE [dbo].[products]  WITH CHECK ADD FOREIGN KEY([category_id])
REFERENCES [dbo].[categories] ([id])
GO
ALTER TABLE [dbo].[products]  WITH CHECK ADD FOREIGN KEY([seller_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[role_features]  WITH CHECK ADD  CONSTRAINT [FK17369effe8mrvk1ugtbrw8wly] FOREIGN KEY([role_id])
REFERENCES [dbo].[roles] ([id])
GO
ALTER TABLE [dbo].[role_features] CHECK CONSTRAINT [FK17369effe8mrvk1ugtbrw8wly]
GO
ALTER TABLE [dbo].[role_features]  WITH CHECK ADD  CONSTRAINT [FK8xtgg4pfqyam4ec1kchx7hjt4] FOREIGN KEY([feature_id])
REFERENCES [dbo].[features] ([id])
GO
ALTER TABLE [dbo].[role_features] CHECK CONSTRAINT [FK8xtgg4pfqyam4ec1kchx7hjt4]
GO
ALTER TABLE [dbo].[salecode]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[user_roles]  WITH CHECK ADD FOREIGN KEY([role_id])
REFERENCES [dbo].[roles] ([id])
GO
ALTER TABLE [dbo].[user_roles]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id])
GO
