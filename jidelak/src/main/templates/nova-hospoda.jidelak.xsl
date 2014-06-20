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
		<restaurant version="1.7">
			<id>praha-nova-hospoda</id>
			<name>Nová Hospoda</name>
			<phone>(+420) 777826112, 608511616</phone>
			<web>http://novahospoda.eu/</web>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>Butovická 24/64</address>
			<zip>150 00</zip>

			<source time="absolute" firstDayOfWeek="Po" encoding="utf8"
				dateFormat="E d.M.y" locale="cs_CZ"
				url="http://www.lunchtime.cz/podnik/7664-nova-hospoda/denni-menu" />

			<open>
				<term day-of-week="Po" from="10:00" to="23:00" />
				<term day-of-week="Út"  from="10:00" to="23:00" />
				<term day-of-week="St" from="10:00" to="23:00" />
				<term day-of-week="Čt" from="10:00" to="23:00" />
				<term day-of-week="Pá" from="10:00" to="00:00" />
				<term day-of-week="So" from="10:30" to="00:00" />
				<term day-of-week="Ne" from="10:30" to="23:00" />
				<term from="10:00" to="14:30" description="polední menu" />
			</open>

			<!-- <xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" 
				/> -->
			<xsl:apply-templates select="//article[@class='facility-daily-menu']" />
		</restaurant>
	</xsl:template>

	<xsl:template match="article[@class='facility-daily-menu']">
		<menu>
			<xsl:apply-templates select=".//section[@class='daily-menu-for-day']" />
		</menu>
	</xsl:template>

	<xsl:template match="section[@class='daily-menu-for-day']">
		<xsl:variable name="date" select="header/h2/text()" />
	
		<xsl:for-each select=".//li[starts-with(@class,'daily-menu-item')]">
			<xsl:choose>
				<xsl:when test="starts-with(normalize-space(span[@class='name']), 'Menu')"></xsl:when>
				<xsl:when test="starts-with(normalize-space(span[@class='name']), 'Polévky')"></xsl:when>
				<xsl:when test="starts-with(normalize-space(span[@class='name']), 'Hlavní jídla')"></xsl:when>
				<xsl:when test="starts-with(normalize-space(span[@class='name']), 'Smažená jídla')"></xsl:when>
				<xsl:when test="starts-with(normalize-space(span[@class='name']), 'Saláty')"></xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select=".">
						<xsl:with-param name="date" select="$date" />
						<xsl:with-param name="pos" select="position()" />
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	
	</xsl:template>

	
	<xsl:template match="li[starts-with(@class,'daily-menu-item')]">
		<xsl:param name="date" />
		<xsl:param name="pos" />
		<meal>
			<xsl:attribute name="dish"><xsl:call-template name="dish" /></xsl:attribute>
			<xsl:attribute name="category"><xsl:call-template name="category" /></xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="$pos" /></xsl:attribute>
			<xsl:attribute name="time"><xsl:value-of select="$date" /></xsl:attribute>
			<xsl:attribute name="ref-time"><xsl:value-of select="$date" /></xsl:attribute>
			<title>
				<xsl:apply-templates select="span[@class='name']//text()" />
			</title>
			<price>
				<xsl:apply-templates select="span[@class='price']//text()" />
			</price>
		</meal>
	</xsl:template>
	
	<xsl:template name="dish">
		<xsl:choose>
			<xsl:when test="./preceding-sibling::li[starts-with(normalize-space(span[@class='name']), 'Hlavní jídla')]">dinner</xsl:when>
			<xsl:when test="./preceding-sibling::li[starts-with(normalize-space(span[@class='name']), 'Polévky')]">soup</xsl:when>
			<xsl:when test="./preceding-sibling::li[starts-with(normalize-space(span[@class='name']), 'Menu')]">menu</xsl:when>
			<xsl:otherwise>soup</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
		
	<xsl:template name="category">
		<xsl:choose>
			<xsl:when test="./preceding-sibling::li[starts-with(normalize-space(span[@class='name']), 'Saláty')]">3-salad</xsl:when>
			<xsl:when test="./preceding-sibling::li[starts-with(normalize-space(span[@class='name']), 'Smažená jídla')]">2-fried</xsl:when>
			<xsl:otherwise>1-normal</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
		
</xsl:stylesheet>