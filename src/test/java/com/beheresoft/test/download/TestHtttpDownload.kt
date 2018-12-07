package com.beheresoft.test.download

import com.beheresoft.download.component.download.http.HttpDownloadBootStrap
import com.beheresoft.download.config.DownloadConfig

fun main(args: Array<String>) {

    HttpDownloadBootStrap("https://nj02all01.baidupcs.com/file/8e21702879edc33305efbe2b7289e0f8?bkt=p3-14008e21702879edc33305efbe2b7289e0f89262d587000000004615&fid=1023426499-250528-553223777798410&time=1544167244&sign=FDTAXGERLQBHSKfW-DCb740ccc5511e5e8fedcff06b081203-dnYQZgm637IQtV6wI7PvDL%2Fd9Jw%3D&to=69&size=17941&sta_dx=17941&sta_cs=0&sta_ft=xlsx&sta_ct=7&sta_mt=7&fm2=MH%2CNanjing02%2CAnywhere%2C%2Cbeijing%2Ccnc&ctime=1446627271&mtime=1446627271&resv0=cdnback&resv1=0&vuk=1023426499&iv=0&htype=&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=14008e21702879edc33305efbe2b7289e0f89262d587000000004615&sl=76480590&expires=8h&rt=pr&r=911582086&mlogid=7900389468307915939&vbdid=95529945&fin=%E9%98%B2%E7%81%AB%E5%A2%99%E6%9D%83%E9%99%90%E7%94%B3%E8%AF%B7%E8%A1%A8_%E9%87%91%E8%9E%8D%E8%B4%A2%E5%8A%A1%E7%A0%94%E5%8F%91%E9%83%A8_%E5%88%98%E8%90%8C.xlsx&fn=%E9%98%B2%E7%81%AB%E5%A2%99%E6%9D%83%E9%99%90%E7%94%B3%E8%AF%B7%E8%A1%A8_%E9%87%91%E8%9E%8D%E8%B4%A2%E5%8A%A1%E7%A0%94%E5%8F%91%E9%83%A8_%E5%88%98%E8%90%8C.xlsx&rtype=1&dp-logid=7900389468307915939&dp-callid=0.1.1&hps=1&tsl=80&csl=80&csign=BashyzqN%2F9%2BAlFS79F9E4vmhNNo%3D&so=0&ut=6&uter=4&serv=0&uc=731867986&ti=548236dbf16cf8fa3f8faaf2bd6e45abd67b6025179e29da&by=themis"
    , DownloadConfig()
    ).start()
}