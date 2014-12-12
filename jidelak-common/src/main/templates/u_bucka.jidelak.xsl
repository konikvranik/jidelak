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
		<restaurant version="2.2">
			<id>praha-u-bucka</id>
			<name>Restaurace U Bůčka</name>
			<phone>(+420) 737 780 745</phone>
			<web>http://www.restauraceubucka.cz/</web>
			<e-mail>info@restauraceubucka.cz</e-mail>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>Na Vidouli 1</address>
			<zip>158 00</zip>

			<source time="absolute" firstDayOfWeek="Po" encoding="utf8" dateFormat="d.M.y" 
				locale="cs_CZ" url="http://www.restauraceubucka.cz/restauraceubucka/3-Denni-nabidka" 
				/>

			<open>
				<term day-of-week="Po" from="10:30" to="23:00" />
				<term day-of-week="Út" from="10:30" to="23:00" />
				<term day-of-week="St" from="10:30" to="23:00" />
				<term day-of-week="Čt" from="10:30" to="23:00" />
				<term day-of-week="Pá" from="10:30" to="23:00" />
				<term day-of-week="So" from="11:00" to="23:00" />
				<term day-of-week="Ne" from="11:00" to="22:00" />
			</open>

			<!-- <xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" 
				/> -->
			<xsl:apply-templates select="//*[@id='incenterpage2']/table" />
		</restaurant>
	</xsl:template>



	<xsl:template match="table">
		<menu>
			<xsl:apply-templates select=".//tr" />
		</menu>
	</xsl:template>



	<xsl:template match="tr">
		<xsl:if test="td[2]//text()">
			<xsl:variable name="date" select="preceding-sibling::tr/td/." />

			<meal>
				<xsl:attribute name="dish"><xsl:call-template
					name="dish" /></xsl:attribute>
				<xsl:attribute name="category"><xsl:call-template
					name="category" /></xsl:attribute>
				<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
				<xsl:attribute name="time"><xsl:value-of select="$date" /></xsl:attribute>
				<xsl:attribute name="ref-time"><xsl:value-of select="$date" /></xsl:attribute>
				<xsl:choose>
					<xsl:when test="contains(td[1]/span//text(), '(')">
						<title>
							<xsl:value-of select="substring-before(td[1]/span,'(')" />
						</title>
						<description>
							<xsl:value-of
								select="substring-before(substring-after(td[1]/span,'('),')')" />
						</description>
					</xsl:when>
					<xsl:otherwise>
						<title>
							<xsl:value-of select="td[1]/span" />
						</title>
					</xsl:otherwise>
				</xsl:choose>
				<price>
					<xsl:apply-templates select="td[2]/span//text()" />
				</price>
			</meal>
		</xsl:if>
	</xsl:template>

	<xsl:template name="dish">
		<xsl:choose>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'HLAVNÍ JÍDLA')]">
				<xsl:text>dinner</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'TEPLÉ PŘEDKRMY')]">
				<xsl:text>starter</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'TEPLÝ PŘEDKRM')]">
				<xsl:text>starter</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'STUDENÉ PŘEDKRMY')]">
				<xsl:text>starter</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'STUDENÝ PŘEDKRM')]">
				<xsl:text>starter</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'POLÉVKY')]">
				<xsl:text>soup</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'Menu')]">
				<xsl:text>menu</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>dinner</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="category">
		<xsl:choose>

			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'SLADKÉ')]">
				<xsl:text>2-sweet</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'TĚSTOVINY')]">
				<xsl:text>4-pasta</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'SALÁTY')]">
				<xsl:text>3-salad</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'VEGETARIÁNSKÉ')]">
				<xsl:text>1-vegetarian</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'SPECIALITA DNE')]">
				<xsl:text>1-live</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'HLAVNÍ JÍDLA')]">
				<xsl:text>1-normal</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'TEPLÉ PŘEDKRMY')]">
				<xsl:text>2-warm</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'TEPLÝ PŘEDKRM')]">
				<xsl:text>2-warm</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'STUDENÉ PŘEDKRMY')]">
				<xsl:text>3-cold</xsl:text>
			</xsl:when>
			<xsl:when
				test="./preceding-sibling::tr[starts-with(normalize-space(td//strong), 'STUDENÝ PŘEDKRM')]">
				<xsl:text>3-cold</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>1-normal</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>