package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;

public class Controller {

	public class MyPointComparable implements Comparator<Point> {

		@Override
		public int compare(Point arg0, Point arg1) {
			return (arg0.getX() > arg1.getX() ? -1 : (arg0.getX() == arg1
					.getX() ? 0 : 1));
		}
	}
	@FXML
	private TextField txtField;
	@FXML
	private ImageView imgView;
	@FXML
	private Button btnBlur;
	@FXML
	private Button btnEdgeDet;
	@FXML
	private Button btnSharpen;
	@FXML
	private Button btnEmboss;
	@FXML
	private Button btnGauss;
	@FXML
	private Canvas canv;
	@FXML
	private Button btnApply;

	List<Point> listPoint = new ArrayList<Point>();

	int[] fun = new int[256];

	@FXML
	private void btnLoad(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"));
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			Image image = new Image("file:" + file.getPath());
			imgView.setImage(image);
			enableButtons();
		}
	}

	@FXML
	private void btBlur() {
		float[][] kernel = new float[][] { { 0.11f, 0.11f, 0.11f },
				{ 0.11f, 0.11f, 0.11f }, { 0.11f, 0.11f, 0.11f } };
		int factor = 1;
		int offset = 0;
		applyFilter(kernel, factor, offset);
	}

	@FXML
	private void btEdgeDet() {
		float[][] kernel = new float[][] { { -1, -1, -1 }, { -1, 8, -1, },
				{ -1, -1, -1 } };
		int factor = 1;
		int offset = 128;
		applyFilter(kernel, factor, offset);
	}

	@FXML
	private void btSharpen() {
		float[][] kernel = new float[][] { { -1, -1, -1 }, { -1, 9, -1 },
				{ -1, -1, -1 } };
		int factor = 1;
		int offset = 0;

		applyFilter(kernel, factor, offset);
	}

	@FXML
	private void btEmboss() {
		float[][] kernel = new float[][] { { -1, -1, -1 }, { 0, 1, 0, },
				{ 1, 1, 1 } };
		int factor = 1;
		int offset = 0;

		applyFilter(kernel, factor, offset);
	}

	@FXML
	private void btGauss() {
		float[][] kernel = new float[][] { { 0, 1, 0 }, { 1, 4, 1, },
				{ 0, 1, 0 } };
		int factor = 8;
		int offset = 0;
		applyFilter(kernel, factor, offset);
	}

	@FXML
	private void btInver() {
		clearCanvas();
		listPoint.clear();
		zeroFun();
		
		final GraphicsContext graphicsContext = canv.getGraphicsContext2D();
		graphicsContext.setFill(Color.GREEN);
		graphicsContext.setStroke(Color.GREEN);
		graphicsContext.setLineWidth(2);
		clearCanvas();
		listPoint.clear();
		listPoint.add(new Point(0, 255));
		listPoint.add(new Point(255,0));
		
		sortList();
		clearCanvas();
		drawLine();
		calculateFun();
	}

	@FXML
	private void btBright() {
		clearCanvas();
		listPoint.clear();
		zeroFun();
		
		final GraphicsContext graphicsContext = canv.getGraphicsContext2D();
		graphicsContext.setFill(Color.GREEN);
		graphicsContext.setStroke(Color.GREEN);
		graphicsContext.setLineWidth(2);
		
		listPoint.add(new Point(0, 0));
		listPoint.add(new Point(40, 0));
		listPoint.add(new Point(255,215));
		
		sortList();
		clearCanvas();
		drawLine();
		calculateFun();
		
	}

	@FXML
	private void btCont() {
		clearCanvas();
		listPoint.clear();
		zeroFun();
		
		final GraphicsContext graphicsContext = canv.getGraphicsContext2D();
		graphicsContext.setFill(Color.GREEN);
		graphicsContext.setStroke(Color.GREEN);
		graphicsContext.setLineWidth(2);
	
		listPoint.add(new Point(40, 0));
		listPoint.add(new Point(215,255));
		listPoint.add(new Point(255,255));
		
		sortList();
		clearCanvas();
		drawLine();
		calculateFun();
		
	}

	@FXML
	private void btApply() {
		applyFilterFun();
	}
	private void applyFilterFun() {
		Image src = imgView.getImage();
		BufferedImage img = ImageFilters.processImageFun(
				javafx.embed.swing.SwingFXUtils.fromFXImage(src, null),fun);
		Image image = SwingFXUtils.toFXImage(img, null);
		imgView.setImage(image);
	}
	@FXML
	private void mPressed() {
		final GraphicsContext graphicsContext = canv.getGraphicsContext2D();
		// initDraw(graphicsContext);
		graphicsContext.setFill(Color.RED);
		graphicsContext.setStroke(Color.RED);
		graphicsContext.setLineWidth(2);
		canv.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {

						graphicsContext.fillOval(e.getX(), e.getY(), 2, 2);
						listPoint.add(new Point(e.getX(), e.getY()));

						// clearCanvas();

					}
				});
		sortList();
		clearCanvas();
		drawLine();
		calculateFun();
	}
	@FXML
	private void btGamma() {
		Image src = imgView.getImage();
		String gam = txtField.getText();
		float gamma = Float.parseFloat(gam);
		BufferedImage img = ImageFilters.processImageFunGamma(
				javafx.embed.swing.SwingFXUtils.fromFXImage(src, null),gamma);
		Image image = SwingFXUtils.toFXImage(img, null);
		imgView.setImage(image);
	}

	@FXML
	private void btClr() {
		clearCanvas();
		listPoint.clear();
	}
	private void zeroFun(){
		for (int x = 0; x < 256; x++) {
			fun[x] = 0;
		}
	}

	private void calculateFun() {
		if (listPoint.size() >= 2) {
			int a, b;
			int x1, x2, y1, y2;
			if(listPoint.size()<1)
			for (int x = 0; x < 256; x++) {
				fun[x] = 0;
			}
			for (int i = 0; i < listPoint.size() - 1; i++) {
				if (listPoint.get(i).getX() != listPoint.get(i + 1).getX()
						&& listPoint.get(i).getY() != listPoint.get(i + 1)
								.getY()) {
					x1 = (int) listPoint.get(i).getX();
					x2 = (int) listPoint.get(i + 1).getX();
					y1 = (int) listPoint.get(i).getY();
					y2 = (int) listPoint.get(i + 1).getY();

					a = (x1 - x2) / (y1 - y2);
					b = y1 - a * x1;

					for (int x = 0; x < 256; x++) {
						if ((x > listPoint.get(i).getX() && x < listPoint
								.get(i).getX())
								|| (x < listPoint.get(i).getX() && x > listPoint
										.get(i + 1).getX())) {
							fun[x] = a * x + b;
						}
					}
				}
			}
		}
	}

	private void clearCanvas() {
		final GraphicsContext graphicsContext = canv.getGraphicsContext2D();
		graphicsContext.clearRect(0, 0, 256, 256);
	}

	private void drawLine() {
		final GraphicsContext graphicsContext = canv.getGraphicsContext2D();
		for (int i = 0; i < listPoint.size() - 1; i++) {
			double x1, x2, y1, y2;
			graphicsContext.beginPath();
			x1 = listPoint.get(i).getX();
			y1 = listPoint.get(i).getY();
			x2 = listPoint.get(i + 1).getX();
			y2 = listPoint.get(i + 1).getY();
			graphicsContext.stroke();
			graphicsContext.moveTo(x1, y1);
			graphicsContext.stroke();

			graphicsContext.lineTo(x2, y2);
			graphicsContext.stroke();
		}
	}

	private void sortList() {

		Collections.sort(listPoint, new MyPointComparable());
	}

	private void enableButtons() {
		btnBlur.disableProperty().set(false);
		btnEdgeDet.disableProperty().set(false);
		btnSharpen.disableProperty().set(false);
		btnEmboss.disableProperty().set(false);
		btnGauss.disableProperty().set(false);
		btnApply.disableProperty().set(false);
	}

	private void applyFilter(float[][] kernel, int fac, int offset) {
		Image src = imgView.getImage();
		BufferedImage img = ImageFilters.processImageConv(
				javafx.embed.swing.SwingFXUtils.fromFXImage(src, null), kernel,
				fac, offset);
		Image image = SwingFXUtils.toFXImage(img, null);
		imgView.setImage(image);
	}
}
