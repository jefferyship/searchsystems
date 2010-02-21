# kwok (kernaling.wong@gmail.com)
#stop serchd and save change to hardisk
{#SphinxPath}/bin/searchd -c {#SphinxPath}/etc/csft.config --stop
echo stopped searchd\n
#start searchd for server of search
{#SphinxPath}/bin/searchd -c {#SphinxPath}/etc/csft.config
echo started searchd\n
#merge delta index to main index for updating including deletion for the record
echo begin to merge delta index to main indx ....\n
{#SphinxPath}/bin/indexer --config {#SphinxPath}/etc/csft.config --merge main delta --rotate --merge-dst-range EN_Status 1 1
echo merge operation complete~!\n
