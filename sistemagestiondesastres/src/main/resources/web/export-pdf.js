// ===== EXPORTAR ESTADÍSTICAS A PDF =====
async function exportToPDF() {
    try {
        // Verificar que jsPDF este disponible
        if (typeof window.jspdf === 'undefined') {
            alert('Error: Libreria jsPDF no esta cargada');
            return;
        }

        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();
        
        const pageWidth = doc.internal.pageSize.getWidth();
        const pageHeight = doc.internal.pageSize.getHeight();
        let yPosition = 20;
        
        // ===== ENCABEZADO =====
        doc.setFillColor(102, 126, 234);
        doc.rect(0, 0, pageWidth, 35, 'F');
        
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(22);
        doc.setFont('helvetica', 'bold');
        doc.text('SISTEMA DE GESTION DE DESASTRES', pageWidth / 2, 15, { align: 'center' });
        
        doc.setFontSize(12);
        doc.setFont('helvetica', 'normal');
        doc.text('Reporte de Estadisticas', pageWidth / 2, 25, { align: 'center' });
        
        // Fecha y hora
        const now = new Date();
        const dateStr = now.toLocaleDateString('es-CO', { 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        doc.setFontSize(9);
        doc.text(`Generado: ${dateStr}`, pageWidth / 2, 31, { align: 'center' });
        
        yPosition = 45;
        
        // ===== ESTADISTICAS GENERALES =====
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(16);
        doc.setFont('helvetica', 'bold');
        doc.text('ESTADISTICAS GENERALES', 15, yPosition);
        yPosition += 10;
        
        // Calcular totales
        const totalZonas = zonesData.length;
        const totalRutas = routesData.length;
        let totalRecursos = 0;
        Object.values(resourcesData).forEach(location => {
            Object.values(location).forEach(qty => totalRecursos += qty);
        });
        const totalEvacuaciones = evacuationsData.length;
        
        // Cuadros de estadisticas
        const stats = [
            { label: 'Zonas Afectadas', value: totalZonas, color: [102, 126, 234] },
            { label: 'Rutas Disponibles', value: totalRutas, color: [118, 75, 162] },
            { label: 'Recursos Totales', value: totalRecursos, color: [16, 185, 129] },
            { label: 'Evacuaciones', value: totalEvacuaciones, color: [239, 68, 68] }
        ];
        
        const boxWidth = 45;
        const boxHeight = 25;
        const startX = 15;
        
        stats.forEach((stat, index) => {
            const x = startX + (index % 4) * (boxWidth + 2);
            const y = yPosition;
            
            // Fondo del cuadro
            doc.setFillColor(...stat.color);
            doc.roundedRect(x, y, boxWidth, boxHeight, 3, 3, 'F');
            
            // Valor
            doc.setTextColor(255, 255, 255);
            doc.setFontSize(20);
            doc.setFont('helvetica', 'bold');
            doc.text(stat.value.toString(), x + boxWidth / 2, y + 12, { align: 'center' });
            
            // Etiqueta
            doc.setFontSize(8);
            doc.setFont('helvetica', 'normal');
            doc.text(stat.label, x + boxWidth / 2, y + 20, { align: 'center' });
        });
        
        yPosition += 35;
        
        // ===== ZONAS DE MAYOR RIESGO =====
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(14);
        doc.setFont('helvetica', 'bold');
        doc.text('ZONAS DE MAYOR RIESGO', 15, yPosition);
        yPosition += 8;
        
        // Ordenar zonas por nivel de riesgo
        const zonasOrdenadas = [...zonesData]
            .sort((a, b) => (b.nivelDeRiesgo || 0) - (a.nivelDeRiesgo || 0))
            .slice(0, 5);
        
        doc.setFontSize(10);
        doc.setFont('helvetica', 'normal');
        
        zonasOrdenadas.forEach((zona, index) => {
            const riesgo = zona.nivelDeRiesgo || 0;
            const color = riesgo > 70 ? [239, 68, 68] : riesgo > 40 ? [245, 158, 11] : [16, 185, 129];
            
            // Barra de riesgo
            doc.setFillColor(240, 240, 240);
            doc.rect(15, yPosition, 100, 6, 'F');
            
            doc.setFillColor(...color);
            doc.rect(15, yPosition, (riesgo / 100) * 100, 6, 'F');
            
            // Información
            doc.setTextColor(0, 0, 0);
            doc.text(`${index + 1}. ${zona.nombre}`, 120, yPosition + 4);
            doc.setTextColor(...color);
            doc.setFont('helvetica', 'bold');
            doc.text(`${riesgo}%`, 180, yPosition + 4);
            
            doc.setFont('helvetica', 'normal');
            yPosition += 10;
        });
        
        yPosition += 5;
        
        // ===== RECURSOS POR TIPO =====
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(14);
        doc.setFont('helvetica', 'bold');
        doc.text('DISTRIBUCION DE RECURSOS', 15, yPosition);
        yPosition += 8;
        
        const resourceTypes = {};
        Object.values(resourcesData).forEach(location => {
            Object.entries(location).forEach(([type, qty]) => {
                resourceTypes[type] = (resourceTypes[type] || 0) + qty;
            });
        });
        
        doc.setFontSize(10);
        doc.setFont('helvetica', 'normal');
        
        const colors = [
            [102, 126, 234],
            [118, 75, 162],
            [16, 185, 129],
            [245, 158, 11],
            [239, 68, 68]
        ];
        
        Object.entries(resourceTypes).forEach(([type, qty], index) => {
            const color = colors[index % colors.length];
            
            // Círculo de color
            doc.setFillColor(...color);
            doc.circle(18, yPosition + 2, 2, 'F');
            
            // Informacion
            doc.setTextColor(0, 0, 0);
            doc.text(`${type}:`, 25, yPosition + 4);
            doc.setFont('helvetica', 'bold');
            doc.text(`${qty} unidades`, 80, yPosition + 4);
            doc.setFont('helvetica', 'normal');
            
            yPosition += 8;
        });
        
        yPosition += 5;
        
        // ===== EVACUACIONES POR PRIORIDAD =====
        if (yPosition > pageHeight - 60) {
            doc.addPage();
            yPosition = 20;
        }
        
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(14);
        doc.setFont('helvetica', 'bold');
        doc.text('EVACUACIONES POR PRIORIDAD', 15, yPosition);
        yPosition += 8;
        
        const priorities = { Alta: 0, Media: 0, Baja: 0 };
        evacuationsData.forEach(evac => {
            if (evac.prioridad > 70) priorities.Alta++;
            else if (evac.prioridad > 40) priorities.Media++;
            else priorities.Baja++;
        });
        
        doc.setFontSize(10);
        
        // Alta prioridad
        doc.setFillColor(239, 68, 68);
        doc.rect(15, yPosition, 30, 10, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFont('helvetica', 'bold');
        doc.text('ALTA', 30, yPosition + 7, { align: 'center' });
        doc.setTextColor(0, 0, 0);
        doc.setFont('helvetica', 'normal');
        doc.text(`: ${priorities.Alta} evacuaciones`, 48, yPosition + 7);
        yPosition += 12;
        
        // Media prioridad
        doc.setFillColor(245, 158, 11);
        doc.rect(15, yPosition, 30, 10, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFont('helvetica', 'bold');
        doc.text('MEDIA', 30, yPosition + 7, { align: 'center' });
        doc.setTextColor(0, 0, 0);
        doc.setFont('helvetica', 'normal');
        doc.text(`: ${priorities.Media} evacuaciones`, 48, yPosition + 7);
        yPosition += 12;
        
        // Baja prioridad
        doc.setFillColor(59, 130, 246);
        doc.rect(15, yPosition, 30, 10, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFont('helvetica', 'bold');
        doc.text('BAJA', 30, yPosition + 7, { align: 'center' });
        doc.setTextColor(0, 0, 0);
        doc.setFont('helvetica', 'normal');
        doc.text(`: ${priorities.Baja} evacuaciones`, 48, yPosition + 7);
        yPosition += 15;
        
        // ===== NUEVA PÁGINA - ANÁLISIS DETALLADO =====
        doc.addPage();
        yPosition = 20;
        
        // Título de sección
        doc.setFillColor(102, 126, 234);
        doc.rect(0, 0, pageWidth, 20, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(16);
        doc.setFont('helvetica', 'bold');
        doc.text('ANALISIS DETALLADO DE EVACUACIONES', pageWidth / 2, 13, { align: 'center' });
        
        yPosition = 30;
        
        // ===== EVACUACIONES PENDIENTES VS COMPLETADAS =====
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(14);
        doc.setFont('helvetica', 'bold');
        doc.text('ESTADO DE EVACUACIONES', 15, yPosition);
        yPosition += 8;
        
        const evacuacionesPendientes = evacuationsData.filter(e => e.estado !== 'Completada').length;
        const evacuacionesCompletadas = evacuationsData.filter(e => e.estado === 'Completada').length;
        
        doc.setFontSize(10);
        doc.setFont('helvetica', 'normal');
        
        // Pendientes
        doc.setFillColor(245, 158, 11);
        doc.roundedRect(15, yPosition, 80, 18, 2, 2, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(16);
        doc.setFont('helvetica', 'bold');
        doc.text(evacuacionesPendientes.toString(), 25, yPosition + 12);
        doc.setFontSize(9);
        doc.setFont('helvetica', 'normal');
        doc.text('Pendientes', 45, yPosition + 12);
        
        // Completadas
        doc.setFillColor(16, 185, 129);
        doc.roundedRect(105, yPosition, 80, 18, 2, 2, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(16);
        doc.setFont('helvetica', 'bold');
        doc.text(evacuacionesCompletadas.toString(), 115, yPosition + 12);
        doc.setFontSize(9);
        doc.setFont('helvetica', 'normal');
        doc.text('Completadas', 135, yPosition + 12);
        
        yPosition += 25;
        
        // ===== EVACUACIONES POR ZONA =====
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(14);
        doc.setFont('helvetica', 'bold');
        doc.text('EVACUACIONES POR ZONA', 15, yPosition);
        yPosition += 8;
        
        const evacuacionesPorZona = {};
        evacuationsData.forEach(evac => {
            const zona = evac.zona || 'Sin especificar';
            evacuacionesPorZona[zona] = (evacuacionesPorZona[zona] || 0) + parseInt(evac.personas || 0);
        });
        
        doc.setFontSize(9);
        doc.setFont('helvetica', 'normal');
        
        Object.entries(evacuacionesPorZona).slice(0, 8).forEach(([zona, personas], index) => {
            const barWidth = (personas / Math.max(...Object.values(evacuacionesPorZona))) * 120;
            
            // Nombre de zona
            doc.setTextColor(0, 0, 0);
            doc.text(zona.substring(0, 25), 15, yPosition + 4);
            
            // Barra
            doc.setFillColor(220, 220, 220);
            doc.rect(75, yPosition, 120, 6, 'F');
            doc.setFillColor(102, 126, 234);
            doc.rect(75, yPosition, barWidth, 6, 'F');
            
            // Cantidad
            doc.setFont('helvetica', 'bold');
            doc.text(`${personas}`, 180, yPosition + 4);
            doc.setFont('helvetica', 'normal');
            
            yPosition += 9;
        });
        
        yPosition += 5;
        
        // ===== ANÁLISIS DE RECURSOS =====
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(14);
        doc.setFont('helvetica', 'bold');
        doc.text('ANALISIS DE RECURSOS POR ZONA', 15, yPosition);
        yPosition += 8;
        
        doc.setFontSize(9);
        doc.setFont('helvetica', 'normal');
        
        // Encabezado de tabla
        doc.setFillColor(102, 126, 234);
        doc.rect(15, yPosition, 180, 8, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFont('helvetica', 'bold');
        doc.text('ZONA', 20, yPosition + 5);
        doc.text('ALIMENTO', 80, yPosition + 5);
        doc.text('MEDICINA', 120, yPosition + 5);
        doc.text('AGUA', 160, yPosition + 5);
        yPosition += 10;
        
        doc.setTextColor(0, 0, 0);
        doc.setFont('helvetica', 'normal');
        
        Object.entries(resourcesData).slice(0, 8).forEach(([zona, recursos], index) => {
            const bg = index % 2 === 0 ? 245 : 255;
            doc.setFillColor(bg, bg, bg);
            doc.rect(15, yPosition - 2, 180, 8, 'F');
            
            doc.text(zona.substring(0, 20), 20, yPosition + 4);
            doc.text((recursos.ALIMENTO || 0).toString(), 85, yPosition + 4);
            doc.text((recursos.MEDICINA || 0).toString(), 125, yPosition + 4);
            doc.text((recursos.AGUA || 0).toString(), 163, yPosition + 4);
            
            yPosition += 8;
        });
        
        yPosition += 10;
        
        // ===== ZONAS CON DÉFICIT DE RECURSOS =====
        doc.setTextColor(239, 68, 68);
        doc.setFontSize(14);
        doc.setFont('helvetica', 'bold');
        doc.text('ALERTAS: ZONAS CON DEFICIT DE RECURSOS', 15, yPosition);
        yPosition += 8;
        
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(9);
        doc.setFont('helvetica', 'normal');
        
        let alertasCount = 0;
        zonesData.forEach(zona => {
            const recursos = resourcesData[zona.nombre] || {};
            const totalRecursosZona = Object.values(recursos).reduce((a, b) => a + b, 0);
            const necesidadEstimada = (zona.poblacion || 0) * 2; // 2 recursos por persona
            
            if (totalRecursosZona < necesidadEstimada && alertasCount < 5) {
                const deficit = necesidadEstimada - totalRecursosZona;
                
                doc.setFillColor(254, 226, 226);
                doc.roundedRect(15, yPosition, 180, 12, 2, 2, 'F');
                
                doc.setTextColor(239, 68, 68);
                doc.circle(20, yPosition + 6, 2, 'F');
                
                doc.setTextColor(0, 0, 0);
                doc.setFont('helvetica', 'bold');
                doc.text(zona.nombre, 25, yPosition + 7);
                doc.setFont('helvetica', 'normal');
                doc.text(`Deficit estimado: ${deficit} unidades`, 120, yPosition + 7);
                
                yPosition += 15;
                alertasCount++;
            }
        });
        
        if (alertasCount === 0) {
            doc.setTextColor(16, 185, 129);
            doc.text('No se detectaron zonas con deficit critico de recursos', 15, yPosition);
            yPosition += 10;
        }
        
        // ===== PIE DE PÁGINA =====
        doc.setFontSize(8);
        doc.setTextColor(128, 128, 128);
        doc.text('Sistema de Gestión de Desastres - Reporte automático', pageWidth / 2, pageHeight - 10, { align: 'center' });
        doc.text(`Usuario: ${currentUser.nombre} (${currentUser.rol})`, pageWidth / 2, pageHeight - 6, { align: 'center' });
        
        // Guardar PDF
        const fileName = `Estadisticas_Desastres_${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')}.pdf`;
        doc.save(fileName);
        
        // Notificacion de exito
        alert(`Reporte exportado exitosamente!\n\nArchivo: ${fileName}\n\nEl PDF ha sido descargado.`);
        
    } catch (error) {
        console.error('Error exportando PDF:', error);
        alert(`Error al exportar PDF:\n\n${error.message}`);
    }
}
