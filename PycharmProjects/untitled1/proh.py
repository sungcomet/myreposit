import requests
from bs4 import BeautifulSoup
from urllib.request import Request, urlopen
import urllib.request
def spider(maxpage):
    page=1
    while page<=maxpage:
         url='http://radibrary.tistory.com/tag/TRYangle%20harmony?page='+str(page)
         source_code=Request(url)
         source_code.add_header("User-Agent",
                            "Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5")
         plain_text=urlopen(source_code).read()
         soup=BeautifulSoup(plain_text,'html.parser')
         for address in soup.find_all("a",{"class":"link_post"}):
             href=address.get('href')
             suburl='http://radibrary.tistory.com'+href
             sourcecode = Request(suburl)
             sourcecode.add_header("User-Agent",
                                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; es-ES; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5")
             plaintext = urlopen(sourcecode).read()
             subsoup = BeautifulSoup(plaintext, 'html.parser')
             for divs in subsoup.findAll('div', {'class': 'moreless_content'}):
                for links in divs.findAll('a'):
                    urllib.request.urlretrieve(links.get('href'),links.text)
                    print(links.text)

         page+=1
spider(1)