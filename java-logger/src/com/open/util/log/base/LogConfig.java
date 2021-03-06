package com.open.util.log.base;


import com.open.net.client.object.TcpAddress;
import com.open.net.client.object.UdpAddress;
import com.open.util.log.util.CfgParser;

import java.util.Arrays;
import java.util.HashMap;

/**
 <br/>
 <br/>配置规则:
 <br/>
 <br/>#------------配置格式说明start--------
 <br/># 1.模块名以[]开始               如:[ENABLE]
 <br/># 2.#号之后语句的表示注释         如: #这是一条注释
 <br/># 3.数组以[]括起来,元素以逗号分隔  如: key=[v1,v2]
 <br/>#------------配置格式说明end----------
 <br/>
 <br/>[Switch]
 <br/> enable = true                                           #配置总开关
 <br/>
 <br/>[Common]
 <br/>common_mode  = 1                                        #0x1代表控制台；0x2代表文件；0x4代表Tcp日志；0x8代表Udp日志
 <br/>common_level = 2                                        #取值有>2;<2;=2 ;     另外2:v日志; 3:d日志;  4:I日志;  5:w日志;  6:e日志
 <br/>common_author= [A,B]                                    #代表AB两个人才可以打日志，其它人的日志不可见
 <br/>common_authorGroup=[A,B,C,D,E]                          #A,B,C,D,E 代表5个人
 <br/>common_tag_formater = (__FILE__:__LINE__) __FUNCTION__ __TAG__
 <br/>
 <br/>#common_tag_formater说明1: __FILE__文件名  __CLASS__类名 __FUNCTION__方法名 __LINE__文件行数 __TAG__用户自定义日志标识
 <br/>#common_tag_formater说明2: 在控制台中如果需要实现点击源文件进行跳转，则可以将__FILE__和__LINE__进行连接(__FILE__:__LINE__)即可
 <br/>
 <br/>[Console]
 <br/>console_log_type = 2                                    #终端输出类型    SYSTEM = 1; LOGCAT = 2;
 <br/>
 <br/>[File]
 <br/>file_name_formater=yyyy-MM-dd_%d                        #文件名格式化2017-1-1_1.txt
 <br/>file_size = 10240000                                    #长度单位是byte ，所以1M的长度是1024*1024
 <br/>file_syn  = true
 <br/>
 <br/>[Net]
 <br/>net_tcp=[192.168.123.1:9999,192.168.123.1:9998]         #tcp配置
 <br/>net_udp=[192.168.123.1:9999]                            #udp配置
 <br/>
 <br/>
 * Created by long on 2017/9/13.
 */

public final class LogConfig {

    public static final int LOG_MODE_CONSOLE = 0x1;
    public static final int LOG_MODE_FILE    = 0x2;
    public static final int LOG_MODE_NET_TCP = 0x4;
    public static final int LOG_MODE_NET_UDP = 0x8;

    public static final int LOG_LEVEL_VERBOSE = 2;
    public static final int LOG_LEVEL_DEBUG   = 3;
    public static final int LOG_LEVEL_INFO    = 4;
    public static final int LOG_LEVEL_WARN    = 5;
    public static final int LOG_LEVEL_ERROR   = 6;

    public static final int COMPARE_TYPE_GT = 1;//大于
    public static final int COMPARE_TYPE_EQ = 2;//等于
    public static final int COMPARE_TYPE_LT = 3;//小于

    public static final String __FILE__     = "__FILE__";
    public static final String __CLASS__    = "__CLASS__";
    public static final String __FUNCTION__ = "__FUNCTION__";
    public static final String __LINE__     = "__LINE__";
    public static final String __TAG__      = "__TAG__";
    public static final String __PID__      = "__PID__";
    public static final String __TID__      = "__TID__";
    public static final String __SID__      = "__SID__";

    public static final int FLAG__FILE__    = 0X0001;
    public static final int FLAG__CLASS__   = 0X0002;
    public static final int FLAG__FUNCTION__= 0X0004;
    public static final int FLAG__LINE__    = 0X0008;
    public static final int FLAG__TAG__     = 0X0010;
    public static final int FLAG__PID__     = 0X0020;
    public static final int FLAG__TID__     = 0X0040;


    //-----------ENABLE----------
    public boolean isEnable = false; //是否开启了日志

    //-----------Console----------
    public int      console_log_type = 1;    //打印日志的方式

    //-----------Common----------
    public int      common_mode = 1;    //打印日志的方式
    public int      common_level = 0;   //哪些等级可以输出
    public String[] common_author;    //哪些作者的日志可以输出,支持64个作者
    public String[] common_authorGroup;//有哪些作者
    public String   common_tag_formater;//日志需要格式化成什么样的

    public int common_compare_type = COMPARE_TYPE_EQ;
    public HashMap<String,Boolean> logAuthorMap;
    public int common_tag_format_flag;

    //-----------File----------
    public String   file_path;
    public String   file_name_formater;//命名规则
    public long     file_size;//每一个文件大小
    public boolean  file_syn;

    //-----------netConfig----------
    public TcpAddress[] net_tcp;
    public UdpAddress[] net_udp;

    public static LogConfig parse(HashMap<String,Object> ret) {
        LogConfig mLogConfig = new LogConfig();
        mLogConfig.init(ret);
        return mLogConfig;
    }

    private void init(HashMap map){
        if(null != map){

            //----------Global----------
            isEnable        = CfgParser.getBoolean(map,"Switch","enable");

            //----------Common----------
            common_mode = CfgParser.getInt(map,"Common","common_mode");
            String _logLevel        = CfgParser.getString(map,"Common","common_level");
            if(_logLevel.startsWith(">")){
                common_compare_type = COMPARE_TYPE_GT;
                common_level = Integer.valueOf(_logLevel.substring(1));
            }else if(_logLevel.startsWith("<")){
                common_compare_type = COMPARE_TYPE_LT;
                common_level = Integer.valueOf(_logLevel.substring(1));
            }else if(_logLevel.startsWith("=")){
                common_compare_type = COMPARE_TYPE_EQ;
                common_level = Integer.valueOf(_logLevel.substring(1));
            }else {
                common_compare_type = COMPARE_TYPE_EQ;
                common_level = Integer.valueOf(_logLevel.substring(0));
            }
            common_tag_formater = CfgParser.getString(map,"Common","common_tag_formater");
            if(common_tag_formater.contains(__FILE__)){
                common_tag_format_flag |= FLAG__FILE__;
            }
            if(common_tag_formater.contains(__CLASS__)){
                common_tag_format_flag |= FLAG__CLASS__;
            }
            if(common_tag_formater.contains(__FUNCTION__)){
                common_tag_format_flag |= FLAG__FUNCTION__;
            }
            if(common_tag_formater.contains(__LINE__)){
                common_tag_format_flag |= FLAG__LINE__;
            }
            if(common_tag_formater.contains(__TAG__)){
                common_tag_format_flag |= FLAG__TAG__;
            }
            if(common_tag_formater.contains(__PID__)){
                common_tag_format_flag |= FLAG__PID__;
            }
            if(common_tag_formater.contains(__TID__)){
                common_tag_format_flag |= FLAG__TID__;
            }
            common_author = CfgParser.getStringArray(map,"Common","common_author");
            common_authorGroup = CfgParser.getStringArray(map,"Common","common_authorGroup");
            if(null != common_author && common_author.length>0){
                logAuthorMap = new HashMap<>(common_author.length);
                for (int i = 0; i < common_author.length; i++) {
                    logAuthorMap.put(common_author[i],true);
                }
            }

            //----------Console----------
            console_log_type = CfgParser.getInt(map,"Console","console_log_type");

            //----------File----------
            file_path = CfgParser.getString(map,"File","file_path");
            file_name_formater = CfgParser.getString(map,"File","file_name_formater");
            file_size = CfgParser.getLong(map,"File","file_size");
            file_syn = CfgParser.getBoolean(map,"File","file_syn");

            //----------Net----------
            String val[]     = CfgParser.getStringArray(map,"Net","net_tcp");
            if(null != val){
                net_tcp = new TcpAddress[val.length];
                for (int i = 0; i < val.length; i++) {
                    String[] v = val[i].split(":");
                    if(v.length>1){
                        net_tcp[i]  = new TcpAddress(v[0],Integer.valueOf(v[1]));
                    }
                }
            }
            val              = CfgParser.getStringArray(map,"Net","net_udp");
            if(null != val){
                net_udp = new UdpAddress[val.length];
                for (int i = 0; i < val.length; i++) {
                    String[] v = val[i].split(":");
                    if(v.length>1){
                        net_udp[i] = new UdpAddress(v[0],Integer.valueOf(v[1]));
                    }
                }
            }
        }
    }

    public boolean isPermit( String author, int level){
        return isPermitAuthor(author) && isPermitLevel(level);
    }

    public boolean isPermitAuthor(String author){
    		if(!isEnable) {
    			return false;
    		}
        if(null == logAuthorMap){
            return false;
        }else{
            Boolean ret = logAuthorMap.get(author);
            return (null != ret) ? ret : false;
        }
    }

    public boolean isPermitLevel(int level){
		if(!isEnable) {
			return false;
		}
        if(common_compare_type == COMPARE_TYPE_GT){
            return level > common_level;
        }else if(common_compare_type == COMPARE_TYPE_LT){
            return level < common_level;
        }else  if(common_compare_type == COMPARE_TYPE_EQ){
            return level == common_level;
        }
        return level == common_level;
    }

    public boolean isCanFormatTag(){
        return common_tag_format_flag > 0;
    }

    public String getTraceInfo(String [] names){
        String ret = common_tag_formater;
        if((common_tag_format_flag & FLAG__FILE__) == FLAG__FILE__){
            ret = ret.replace(__FILE__,names[ILog.INDEX_FILENAME]);
        }

        if((common_tag_format_flag & FLAG__CLASS__) == FLAG__CLASS__){
            ret = ret.replace(__CLASS__,names[ILog.INDEX_CLASSNAME]);
        }

        if((common_tag_format_flag & FLAG__FUNCTION__) == FLAG__FUNCTION__){
            ret = ret.replace(__FUNCTION__,names[ILog.INDEX_METHODNAME]);
        }

        if((common_tag_format_flag & FLAG__LINE__) == FLAG__LINE__){
            ret = ret.replace(__LINE__,names[ILog.INDEX_LINENUMBER]);
        }

        if((common_tag_format_flag & FLAG__PID__) == FLAG__PID__){
            ret = ret.replace(__PID__,names[ILog.INDEX_PID]);
        }

        if((common_tag_format_flag & FLAG__TID__) == FLAG__TID__){
            ret = ret.replace(__TID__,names[ILog.INDEX_TID]);
        }
        return ret;
    }

    @Override
    public String toString() {
        return "LogConfig{" +
                "isEnable=" + isEnable +
                ", console_log_type=" + console_log_type +
                ", common_mode=" + common_mode +
                ", common_level=" + common_level +
                ", common_author=" + Arrays.toString(common_author) +
                ", common_authorGroup=" + Arrays.toString(common_authorGroup) +
                ", common_tag_formater='" + common_tag_formater + '\'' +
                ", common_compare_type=" + common_compare_type +
                ", logAuthorMap=" + logAuthorMap +
                ", common_tag_format_flag=" + common_tag_format_flag +
                ", file_name_formater='" + file_name_formater + '\'' +
                ", file_size=" + file_size +
                ", file_syn=" + file_syn +
                ", net_tcp=" + Arrays.toString(net_tcp) +
                ", net_udp=" + Arrays.toString(net_udp) +
                '}';
    }
}
