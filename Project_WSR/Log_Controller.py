class Log_Controller(object):
    
    #File Line Numbers
    POWER_LINE = 1
    MODE_LINE = 2
    PAIRED_LINE = 3
	
    Log_Dictionary = {}

    def __init__(self,file_location):
        self.log_file_Location = file_location

    def get_logs(self):
	
        lines = []
        log_file_in = open(self.log_file_Location, 'rt')
        lines = log_file_in.readlines()
        log_file_in.close()
		
        self.Log_Dictionary["POWER"] = int(lines[self.POWER_LINE].split(" ")[2].strip())
        self.Log_Dictionary["MODE"] = int(lines[self.MODE_LINE].split(" ")[2].strip())
        self.Log_Dictionary["PAIRED"] = int(lines[self.PAIRED_LINE].split(" ")[2].strip())
		
        return self.Log_Dictionary

    def set_logs(self,Log_Dict_temp):
	
        self.Log_Dictionary["POWER"] = Log_Dict_temp["POWER"]
        self.Log_Dictionary["MODE"] = Log_Dict_temp["MODE"]
        self.Log_Dictionary["PAIRED"] = Log_Dict_temp["PAIRED"] 
        
        lines = []
        log_file_in = open(self.log_file_Location, 'rt')
        lines = log_file_in.readlines()
        log_file_in.close()
		
        lineSplitList = []
        
        for line in lines:
            lineSplitList.append(line.split(" "))
        
        lineSplitList[self.POWER_LINE][2] = str(Log_Dict_temp["POWER"])+"\n"
        lineSplitList[self.MODE_LINE][2] = str(Log_Dict_temp["MODE"])+"\n"
        lineSplitList[self.PAIRED_LINE][2] = str(Log_Dict_temp["PAIRED"])+"\n"
        
        #write Modified List to File
        with open(self.log_file_Location, 'w') as log_file_out:
            for item in lineSplitList:
                log_file_out.write("%s" % (" ".join(item)) )
        
        #Done

if __name__ == "__main__":
    lg = Log_Controller("Logs.txt")
    LogDict = { "POWER" : 0, "MODE" : 3, "PAIRED" : 1 }
    lg.set_logs(LogDict)    
    #done
