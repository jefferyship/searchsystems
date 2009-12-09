# kwok (kernaling.wong@gmail.com)
#stop serchd and save change to hardisk
/usr/local/coreseek/bin/searchd --stop
#start searchd for server of search
/usr/local/coreseek/bin/searchd --start
#merge delta index to main index for updating including deletion for the record
/usr/local/coreseek/bin/indexer --merge main delta --rotate --merge-filter-range deleted