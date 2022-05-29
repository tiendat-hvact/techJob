alter table jobs
	add deadline date not null after requirement;

alter table jobs modify experience text not null;