select * from phone;

select distinct phonenum from phone;
select count(phonenum) from phone where phonenum = '01051594355' and sendreceive = 'receive';
select count(phonenum) from phone where phonenum = '01051594355' and sendreceive = 'send';

select count(phonenum) from phone where phonenum = '01025084355' and sendreceive = 'send';
select count(phonenum) from phone where phonenum = '01025084355' and sendreceive = 'receive';

select filename, memo, date from phone where phonenum = '01051594355';

create view group_list as
select
	phonenum as 'phonenumber',
	(select count(phonenum) from phone as p1 where p1.phonenum = p.phonenum and sendreceive = 'send') as 'send',
	(select count(phonenum) from phone as p1 where p1.phonenum = p.phonenum and sendreceive = 'receive') as 'receive'
from phone as p group by phonenum;

desc phone;

select * from group_list;


select
	phonenum as 'phonenumber',
	(select sendreceive from phone as p1 where p1.phonenum = p.phonenum) as 'sendreceive'
from phone as p;

select sendreceive, filename, memo from phone where phonenum = '01051594355';


create table test (
	_id integer primary key autoincrement,
	phonenum text,
	favorite bool
);

insert into test values(0,'aaa', 0);

select * from test;







create table phoneTest (
	_id integer primary key autoincrement, 
	phonenum text,
	sendreceive text,
	filename text,
	memo text,
	date text,
	duration text,
	favorite integer,
	save integer
);

insert into phoneTest values(0, '01051594355', 'in', '1.mp3', null, '1', '10', 1, 0);
insert into phoneTest values(1, '114', 'out', '2.mp3', null, '2', '10', 0, 1);
insert into phoneTest values(2, '114', 'in', '3.mp3', null, '3', '10', 0, 1);
insert into phoneTest values(3, '01051594355', 'out', '4.mp3', null, '4', '10', 0, 0);
insert into phoneTest values(4, '01051594355', 'out', '5.mp3', null, '5', '10', 1, 0);
insert into phoneTest values(5, '01022856905', 'out', '6.mp3', null, '6', '10', 0, 1);
insert into phoneTest values(6, '01022856905', 'in', '7.mp3', null, '7', '10', 1, 0);

select * from phoneTest;


create view groups_favorite1 as
select
	phonenum as 'phonenumber',
	(select count(phonenum) from phoneTest as p1 where p1.phonenum = p.phonenum and sendreceive = 'out' and favorite = 1) as 'out',
	(select count(phonenum) from phoneTest as p1 where p1.phonenum = p.phonenum and sendreceive = 'in' and favorite = 1) as 'in'
from phoneTest as p where favorite = 1 group by phonenum;

update phoneTest set favorite = 1 where filename = '3.mp3';
select * from groups_favorite1;

select sendreceive, filename, memo, duration from phoneTest where phonenum = '01051594355' and favorite = 1;


