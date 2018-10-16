# HTTP API Latency with curl

## Requirements 

1. shell of choice
2. curl


## Instructions

Create file *curl-format.txt*  with content bellow:

```
time_namelookup:  %{time_namelookup}\n
       time_connect:  %{time_connect}\n
    time_appconnect:  %{time_appconnect}\n
   time_pretransfer:  %{time_pretransfer}\n
      time_redirect:  %{time_redirect}\n
 time_starttransfer:  %{time_starttransfer}\n
                    ----------\n
         time_total:  %{time_total}\n
```
or alternatively download file from this repo.

Once the file is there, curl can be used like this:

```
curl -w "@curl-format.txt" -o /dev/null -s  http://some-server/some-url
```

and output will look like this:
```
curl -w "@curl-format.txt" -o /dev/null -s  http://localhost:8080/game/a50c0915-2e29-4a50-a7dc-fa72865c4949
time_namelookup:  0.004431
       time_connect:  0.004877
    time_appconnect:  0.000000
   time_pretransfer:  0.004937
      time_redirect:  0.000000
 time_starttransfer:  0.067461
                    ----------
         time_total:  0.067535

```
Parameters:
1. -w specify output format
2. -o directs output to /dev/null as we dont care here about the content
3. -s ask curl to be silent 
