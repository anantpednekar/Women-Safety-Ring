#!/usr/bin/env python3
from threading import *
import bluetooth
import picamera
import time
import base64
from PIL import Image
import RPi.GPIO as GPIO

from WSR_Bluetoothctl_Controller import *
from Log_Controller import *

#make an object of WSR_Bluetoothctl_Controller class
CTRL_BT = WSR_Bluetoothctl_Controller()

#make an object of Log_Controller class
My_Logger = Log_Controller("Logs.txt")

"""
#All logging Stuff

# Power Status
POWER_OFF = 0
POWER_ON = 1

# Modes Status
PARING_MODE = 0
CONNECTED_MODE = 1
PANIC_MODE = 2
IDLE_MODE = 3

# Paired Status
DEVICE_PAIRED = 1
DEVICE_UNPAIRED = 0

"""

#Create Local Log Dictionary
#Get Saved Logs
Ring = My_Logger.get_logs()

#GPIO BOARD Referencing
GPIO.setmode(GPIO.BOARD)

#declare Socket
mclient_socket = 0
mserver_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)

buttonPin = 16
elapsed = 0

# For Button one END to PIN 16 Another to POSITIVE 
GPIO.setup(buttonPin, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)

#Calculates the Press Duration of the Pushbutton
def PushButtonDetect(channel):
    global start
    global end
    global elapsed
    print("Pushbutton Pressed : ",end='')
    if GPIO.input(buttonPin) == 1:
        start = time.time()
    if GPIO.input(buttonPin) == 0:
        end = time.time()
        elapsed = int(end-start)
        print(elapsed)

#sets an Event that runs in background to Detect Button Press
GPIO.add_event_detect(buttonPin, GPIO.BOTH, callback = PushButtonDetect, bouncetime = 20)

#should be called in Thread Only
def CheckPairedStatus():
    global CTRL_BT
    global Ring
    while(True):
        if(CTRL_BT.isPaired()):
            Ring["PAIRED"] = 1
        else:
            Ring["PAIRED"] = 0

#Thread That Constantly Monitors For Pairing Status
#daemon = True for long running Thread
thread_BPC = Thread(target = CheckPairedStatus, daemon = True) 
thread_BPC.setName("Background_Pair_Checker")  
thread_BPC.start()

#Listens for incoming Bluetooth connect Request and returns Socket
def MakeBluetoothConnection():
    global Ring
    global mserver_sock
    global mclient_socket
    server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
    server_sock.bind(("", bluetooth.PORT_ANY))
    server_sock.listen(1)
    port = server_sock.getsockname()[1]
    print("Waiting for connection on RFCOMM channel", port)
    # makes a blocking call
    client_socket, client_info = server_sock.accept()
    print("Accepted connection from : ", client_info)
    mclient_socket = client_socket
    Ring["MODE"] = 1
    return client_socket

# Waits till any Device is Paired ( Removes Previously Paired )
def Pairing():
    global CTRL_BT
    global Ring
    global mclient_socket
    print("Pairing Started")
    CTRL_BT.Make_Power_On()
    CTRL_BT.Remove_paired_devices()
    CTRL_BT.Make_Pairable()
    Ring["MODE"] = 0
    while(True):
        if(Ring["PAIRED"] == 1):
            mclient_socket = MakeBluetoothConnection()
            break
    #done

#captures Images From Picam
def CaptureImageFromPiCam():
    camera = picamera.PiCamera()
    print("Camera started")
    #camera.start_preview()
    for i in range(5):
        #print(" Image %s" %i)
        camera.capture('Images/image%s.jpeg' % i)
        time.sleep(5)
    camera.close()
    #camera.stop_preview()
    #Done

def SendImagesToApp():
    global mclient_socket
    global Ring
    print("Sending to Device " )
    for i in range(5):
        Location = "Images/image%s.jpeg"%i
        picture = Image.open(Location)
        #Compress the Image and save
        Location_Compress = "Images/Compress_image%s.jpeg"%i
        picture.save(Location_Compress,"JPEG",optimize=True,quality=85)
        picture.close()
        #open the image as binary in read mode
        compressed_image = open(Location_Compress, "rb")
        #convert it to base 64 String and save it in a file
        encoded_string = base64.b64encode(compressed_image.read())
        compressed_image.close()
        text_file = open("Images/EncodedString%s.txt"%i, "wb")
        text_file.write(encoded_string)
        text_file.close()
        #Find the Number of transmissions required (count)
        ChunkSize = 990
        text_file = open("Images/EncodedString%s.txt"%i, "rb")
        count = 0
        while True:
            data = text_file.read(ChunkSize)
            if not data:
                break
            count = count+1
        text_file.close()
		
        print("Sending %s image" % i)
        print("No of Segments to send : ",end='')
        print(count)
        j = 1
        text_file = open("Images/EncodedString%s.txt"%i, "rb")
        try:
            #send Count 
            data = str(count).encode()
            mclient_socket.send(data)
            #print(" sent : ",end='')
            #print(data)
            while True:
                j = j+1
                data = text_file.read(ChunkSize)
                #if file Empty break While Move to Next
                if not data:
                    break
                mclient_socket.send(data)
                #Recieve WSR_ACK
                data = mclient_socket.recv(10)
        except OSError:
            print("Disconnected")
            Ring["MODE"] = 3
        #Recieve WSR_DNE
        data = "".encode()
        data = mclient_socket.recv(10)
        print(" Recieved : ",end='')
        print(data)  
        text_file.close()
    #over


#The Main Function
def main():
    global elapsed
    global CTRL_BT
    global Ring
    while True:
        #print(elapsed)
        if elapsed >= 2 and  elapsed <= 3:
            print('Panic MODE')
            elapsed  = 0
            #Check for Connected 
            if(Ring["MODE"] == 1):
                print('Panic MODE E')
                thread_CaptureImg = Thread(target = CaptureImageFromPiCam) 
                thread_CaptureImg.setName("CaptureImg")
                thread_CaptureImg.start()
                #wait for some time
                time.sleep(15)
                thread_ImgBTsender = Thread(target = SendImagesToApp) 
                thread_ImgBTsender.setName("ImageSender")  
                thread_ImgBTsender.start()    
            else:
                print('Need To Connect First')
                #Pairing Mode????
            #time.sleep(1)
			
        elif  elapsed >= 4 and  elapsed <= 5:
		
            elapsed = 0
            print('Power ON/OFF MODE')
            if(not bool(Ring)):
                Ring = My_Logger.get_logs()
            else:
                My_Logger.set_logs(Ring)

            if(Ring["POWER"] == 0):
                print('Powering ON')
                elapsed  = 0
                Ring["POWER"] = 1
                Ring["MODE"] = 3
                CTRL_BT.Make_Power_On()
                
                if(Ring["PAIRED"] == 0):
                    #Start Pairing Mode
                    Pairing()
                else:
                    mclient_socket = MakeBluetoothConnection()

            else:
                print('Powering OFF')
                elapsed  = 0
                Ring["POWER"] = 0
                Ring["MODE"] = 3
                #print(Ring)
                My_Logger.set_logs(Ring)
                CTRL_BT.Make_Power_Off()
                elapsed  = 0
            #time.sleep(1)
                
        elif elapsed >= 6 and  elapsed <= 7:
            elapsed  = 0
            Ring["MODE"] = 0
            print('Pairing MODE')
            Pairing()
            elapsed  = 0
    
if __name__ == "__main__":
    main()