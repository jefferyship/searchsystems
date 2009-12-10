# kwok (kernaling.wong@gmail.com)
#stop serchd and save change to hardisk
/web/software/coreseek/bin/searchd --stop
echo stopped searchd\n
#start searchd for server of search
/web/software/coreseek/bin/searchd
echo started searchd\n
#merge delta index to main index for updating including deletion for the record
echo begin to merge delta index to main indx ....\n
/web/software/coreseek/bin/indexer --merge main delta --rotate --merge-filter-range deleted 1000000000000,2000000000000
echo merge operation complete~!\n
