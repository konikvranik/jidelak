<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" method="xml" encoding="UTF-8" />

	<xsl:template match="/">
		<jidelak>
			<xsl:apply-templates select="/jidelak/config|/html" />
		</jidelak>
	</xsl:template>

	<xsl:template match="/jidelak/config|/html">
		<config>
			<xsl:call-template name="restaurant" />
		</config>
	</xsl:template>

	<xsl:template name="restaurant">
		<restaurant version="1.1">
			<id>praha-na_kopci</id>
			<name>RESTAURACE &amp; TERASA NA KOPCI</name>
			<phone>+420 251 553 102 </phone>
			<e-mail>info@nakopci.com</e-mail>
			<web>http://www.nakopci.com/</web>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>K Závěrce 2774/20</address>
			<zip>150 00</zip>

			<source time="absolute" firstDayOfWeek="Po" encoding="utf8"
				locale="cs_CZ" url="http://www.nakopci.com/menu-restaurace-na-kopci.html" />
			<open>
				<term day-of-week="Po" from="11:30" to="14:30" />
				<term day-of-week="Po" from="17:30" to="23:00" />
				<term day-of-week="Út" from="11:30" to="14:30" />
				<term day-of-week="Út" from="17:30" to="23:00" />
				<term day-of-week="St" from="11:30" to="14:30" />
				<term day-of-week="St" from="17:30" to="23:00" />
				<term day-of-week="Čt" from="11:30" to="14:30" />
				<term day-of-week="Čt" from="17:30" to="23:00" />
				<term day-of-week="Pá" from="11:30" to="14:30" />
				<term day-of-week="Pá" from="17:30" to="23:00" />
				<term day-of-week="So" from="11:30" to="23:00" />
				<term day-of-week="Ne" from="11:30" to="23:00" />
				<term to="15:00" description="polední menu" />
			</open>

			<!-- <xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" 
				/> -->
			<xsl:apply-templates select="//*[@id='mainContainer']" />
		</restaurant>
	</xsl:template>

	<xsl:template match="*[@id='mainContainer']">
		<menu>
			<xsl:apply-templates select=".//div[starts-with(@class,'pageSection')]" />
		</menu>
	</xsl:template>

	<xsl:template match="*[starts-with(@class,'pageSection')]">
		<xsl:choose>
			<xsl:when test="h2[starts-with(.,'ČTYŘCHODOVÉ DEGUSTAČNÍ MENU')] and 1=0">
				<meal dish="dinner" category="99-menu">
					<title>
						<xsl:apply-templates select="h2/text()" />
					</title>
					<description>
						<xsl:apply-templates select="div" />
					</description>
					<price>
						<xsl:apply-templates select="h2/span" />
					</price>
				</meal>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select=".//div[starts-with(@class,'menuItem')]" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="div[starts-with(@class,'menuItem')]">
		<xsl:if test="preceding-sibling::h2[starts-with(text(), 'Denní menu')]">
			<meal>
				<xsl:apply-templates select="." mode="dish" />
				<xsl:apply-templates select="div[starts-with(@class, 'menuName')]" />
			</meal>
		</xsl:if>
	</xsl:template>

	<xsl:template match="div[starts-with(@class, 'menuName')]">
		<title>
			<xsl:apply-templates select="text()" />
		</title>
		<description>
			<xsl:apply-templates select="em/text()" />
		</description>
		<price>
			<xsl:apply-templates select="span[starts-with(@class, 'menuPrice')]" />
		</price>
	</xsl:template>

	<xsl:template match="div[starts-with(@class, 'menuItem')]"
		mode="dish">
		<xsl:choose>
			<xsl:when test="preceding-sibling::h2[starts-with(text(), 'Nápoje')]">
				<xsl:attribute name="dish">drink</xsl:attribute>
				<xsl:apply-templates
					select="(preceding-sibling::div[starts-with(@class, 'menuSubSection')]|*[starts-with(@class, 'menuSubSection')])[position() = last()]"
					mode="drink" />
			</xsl:when>
			<xsl:when
				test="preceding-sibling::h2[starts-with(text(), 'Vinný lístek')]">
				<xsl:attribute name="dish">wine</xsl:attribute>
				<xsl:apply-templates
					select="(preceding-sibling::div[starts-with(@class, 'menuSubSection')]|*[starts-with(@class, 'menuSubSection')])[position() = last()]"
					mode="wine" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates
					select="(preceding-sibling::div[starts-with(@class, 'menuSubSection')]|*[starts-with(@class, 'menuSubSection')])[position() = last()]"
					mode="dish" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="div[starts-with(@class, 'menuSubSection')]"
		mode="dish">
		<xsl:attribute name="dish"><xsl:choose>
		<xsl:when test="starts-with(.,'Polévka')">soup</xsl:when>
		<xsl:when test="starts-with(.,'POLÉVKA')">soup</xsl:when>
		<xsl:when test="starts-with(.,'polévka')">soup</xsl:when>
		
		<xsl:when test="starts-with(.,'HLAVNÍ JÍDLO')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'Hlavní jídlo')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'hlavní jídlo')">dinner</xsl:when>
		
		<xsl:when test="starts-with(.,'PŘEDKRMY')">starter</xsl:when>
		<xsl:when test="starts-with(.,'Předkrmy')">starter</xsl:when>
		<xsl:when test="starts-with(.,'předkrmy')">starter</xsl:when>
		
		<xsl:when test="starts-with(.,'SALÁTY')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'Saláty')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'saláty')">dinner</xsl:when>
		
		<xsl:when test="starts-with(.,'POLÉVKY')">soup</xsl:when>
		<xsl:when test="starts-with(.,'Polévky')">soup</xsl:when>
		<xsl:when test="starts-with(.,'polévky')">soup</xsl:when>
		
		<xsl:when test="starts-with(.,'RYBY &amp; KREVETY')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'Ryby &amp; krevety')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'Ryby &amp; Krevety')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'ryby &amp; krevety')">dinner</xsl:when>
		
		<xsl:when test="starts-with(.,'HLAVNÍ JÍDLA')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'Hlavní jídla')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'hlavní jídla')">dinner</xsl:when>
		
		<xsl:when test="starts-with(.,'DEZERTY &amp; SÝRY')">dessert</xsl:when>
		<xsl:when test="starts-with(.,'Dezerty &amp; sýry')">dessert</xsl:when>
		<xsl:when test="starts-with(.,'Dezerty &amp; Sýry')">dessert</xsl:when>
		<xsl:when test="starts-with(.,'dezerty &amp; sýry')">dessert</xsl:when>
		
		<xsl:when test="starts-with(.,'DĚTSKÉ MENU')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'Dětské menu')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'Dětské Menu')">dinner</xsl:when>
		<xsl:when test="starts-with(.,'dětské menu')">dinner</xsl:when>

		<xsl:otherwise>dinner</xsl:otherwise>
		</xsl:choose></xsl:attribute>
		<xsl:attribute name="category"><xsl:choose>
		<xsl:when test="starts-with(.,'Polévka')">1-daily</xsl:when>
		<xsl:when test="starts-with(.,'POLÉVKA')">1-daily</xsl:when>
		<xsl:when test="starts-with(.,'polévka')">1-daily</xsl:when>
		
		<xsl:when test="starts-with(.,'HLAVNÍ JÍDLO')">1-daily</xsl:when>
		<xsl:when test="starts-with(.,'Hlavní jídlo')">1-daily</xsl:when>
		<xsl:when test="starts-with(.,'hlavní jídlo')">1-daily</xsl:when>
		
		<xsl:when test="starts-with(.,'PŘEDKRMY')">1-normal</xsl:when>
		<xsl:when test="starts-with(.,'Předkrmy')">1-normal</xsl:when>
		<xsl:when test="starts-with(.,'předkrmy')">1-normal</xsl:when>
		
		<xsl:when test="starts-with(.,'SALÁTY')">2-salad</xsl:when>
		<xsl:when test="starts-with(.,'Saláty')">2-salad</xsl:when>
		<xsl:when test="starts-with(.,'saláty')">2-salad</xsl:when>
		
		<xsl:when test="starts-with(.,'POLÉVKY')">2-normal</xsl:when>
		<xsl:when test="starts-with(.,'Polévky')">2-normal</xsl:when>
		<xsl:when test="starts-with(.,'polévky')">2-normal</xsl:when>
		
		<xsl:when test="starts-with(.,'RYBY &amp; KREVETY')">3-fish</xsl:when>
		<xsl:when test="starts-with(.,'Ryby &amp; krevety')">3-fish</xsl:when>
		<xsl:when test="starts-with(.,'Ryby &amp; Krevety')">3-fish</xsl:when>
		<xsl:when test="starts-with(.,'ryby &amp; krevety')">3-fish</xsl:when>
		
		<xsl:when test="starts-with(.,'HLAVNÍ JÍDLA')">4-normal</xsl:when>
		<xsl:when test="starts-with(.,'Hlavní jídla')">4-normal</xsl:when>
		<xsl:when test="starts-with(.,'hlavní jídla')">4-normal</xsl:when>
		
		<xsl:when test="starts-with(.,'DEZERTY &amp; SÝRY')">1-normal</xsl:when>
		<xsl:when test="starts-with(.,'Dezerty &amp; sýry')">1-normal</xsl:when>
		<xsl:when test="starts-with(.,'Dezerty &amp; Sýry')">1-normal</xsl:when>
		<xsl:when test="starts-with(.,'dezerty &amp; sýry')">1-normal</xsl:when>
		
		<xsl:when test="starts-with(.,'DĚTSKÉ MENU')">5-half</xsl:when>
		<xsl:when test="starts-with(.,'Dětské menu')">5-half</xsl:when>
		<xsl:when test="starts-with(.,'Dětské Menu')">5-half</xsl:when>
		<xsl:when test="starts-with(.,'dětské menu')">5-half</xsl:when>
		
		<xsl:otherwise>9-normal</xsl:otherwise>
		</xsl:choose></xsl:attribute>
	</xsl:template>

	<xsl:template match="div[starts-with(@class, 'menuSubSection')]"
		mode="drink">
		<xsl:attribute name="category"><xsl:choose>
		<xsl:when test="starts-with(.,'FENTIMANS ORGANIC')">01-organic-limonade</xsl:when>
		<xsl:when test="starts-with(.,'LIMONÁDA')">02-limonade</xsl:when>
		<xsl:when test="starts-with(.,'JUICE')">03-juice</xsl:when>
		<xsl:when test="starts-with(.,'MINERÁLNÍ VODA')">04-mineral-water</xsl:when>
		<xsl:when test="starts-with(.,'NAŠE NABÍDKA APERITIVŮ')">05-aperitive</xsl:when>
		<xsl:when test="starts-with(.,'TOČENÁ PIVA')">06-draught-beer</xsl:when>
		<xsl:when test="starts-with(.,'LAHVOVÁ PIVA')">07-bottled-beer</xsl:when>
		<xsl:when test="starts-with(.,'ROZLÉVANÁ VÍNA')">08-wine</xsl:when>
		<xsl:when test="starts-with(.,'BÍLÁ VÍNA')">09-white-wine</xsl:when>
		<xsl:when test="starts-with(.,'ČERVENÁ VÍNA')">10-red-wine</xsl:when>
		<xsl:when test="starts-with(.,'KÁVA')">11-caffee</xsl:when>
		<xsl:when test="starts-with(.,'ČAJE')">12-tee</xsl:when>
		<xsl:when test="starts-with(.,'PORTSKÉ ')">13-port</xsl:when>
		<xsl:when test="starts-with(.,'PASTIS ')">14-pastis</xsl:when>
		<xsl:when test="starts-with(.,'LIKÉRY ')">15-liquors</xsl:when>
		<xsl:when test="starts-with(.,'VODKA')">16-vodka</xsl:when>
		<xsl:when test="starts-with(.,'GIN')">17-gin</xsl:when>
		<xsl:when test="starts-with(.,'TEQUILA ')">18-tequila</xsl:when>
		<xsl:when test="starts-with(.,'GRAPPA')">19-grappa</xsl:when>
		<xsl:when test="starts-with(.,'DESTILÁTY')">20-distilate</xsl:when>
		<xsl:when test="starts-with(.,'RUM')">21-rum</xsl:when>
		<xsl:when test="starts-with(.,'CALVADOS')">22-calvados</xsl:when>
		<xsl:when test="starts-with(.,'COGNAC')">23-cognac</xsl:when>
		<xsl:when test="starts-with(.,'ARMAGNAC')">24-armagnac</xsl:when>
		<xsl:when test="starts-with(.,'WHISKY')">25-whisky</xsl:when>
		<xsl:when test="starts-with(.,'KOKTEJLY')">26-coctails</xsl:when>
		<xsl:otherwise>99-other</xsl:otherwise>
		</xsl:choose></xsl:attribute>
	</xsl:template>

	<xsl:template match="div[starts-with(@class, 'menuSubSection')]"
		mode="wine">
		<xsl:attribute name="category"><xsl:choose>
		<xsl:when test="starts-with(.,'ŠUMIVÁ VÍNA')">01-sparkling-wine</xsl:when>
		<xsl:when test="starts-with(.,'BÍLÁ VÍNA')">02-white-wine</xsl:when>
		<xsl:when test="starts-with(.,'ČERVENÁ VÍNA')">03-red-wine</xsl:when>
		<xsl:when test="starts-with(.,'RŮŽOVÁ VÍNA')">04-rose-wine</xsl:when>
		<xsl:when test="starts-with(.,'DEZERTNÍ VÍNA')">05-desert-wine</xsl:when>
		<xsl:otherwise>99-other</xsl:otherwise>
		</xsl:choose></xsl:attribute>
	</xsl:template>
</xsl:stylesheet>